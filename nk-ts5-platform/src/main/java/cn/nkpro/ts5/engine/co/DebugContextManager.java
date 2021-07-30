package cn.nkpro.ts5.engine.co;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.config.security.SecurityUtilz;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.ScriptDefHV;
import cn.nkpro.ts5.exception.TfmsException;
import cn.nkpro.ts5.utils.GroovyUtils;
import cn.nkpro.ts5.utils.OsUtils;
import groovy.lang.GroovyObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class DebugContextManager implements ApplicationContextAware {

    private final static Map<String, ApplicationContext> debugApplications = new ConcurrentHashMap<>();
    private final static ThreadLocal<String>                  localDebugId = new ThreadLocal<>();

    private ApplicationContext                  rootApplicationContext;

    @Autowired@SuppressWarnings("all")
    private RedisSupport<Object>                redisForResoure;
    @Autowired@SuppressWarnings("all")
    private RedisSupport<ContextDescribe>       redisForContext;

    /**
     * 返回上下文列表
     */
    public Collection<ContextDescribe> getDebugContextList(){
        Map<String, ContextDescribe> absent = redisForContext.getHashIfAbsent(Constants.CACHE_DEBUG_CONTEXT, () -> null);
        if(absent!=null){
            return absent.values().stream()
                    .peek(item-> item.setVisible(StringUtils.equals(item.getMac(),OsUtils.getMacAddress()))).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 创建上下文
     */
    public String createContext(String desc){
        String debugId = UUID.randomUUID().toString();

        // 创建Spring上下文

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setParent(rootApplicationContext);
        context.refresh();
        debugApplications.put(debugId,context);

        // 创建调试上下文
        redisForContext.putHash(
                Constants.CACHE_DEBUG_CONTEXT,
                debugId,
                new ContextDescribe(debugId,OsUtils.getMacAddress(),SecurityUtilz.getUser().getUsername(),desc,true)
        );

        return debugId;
    }

    /**
     * 停止一个上下文
     */
    public void removeContext(String debugId){
        ApplicationContext context = debugApplications.remove(debugId);
        if(context!=null){
            ((GenericApplicationContext)context).stop();
        }
        redisForResoure.delete(String.format("DEBUG:%s", debugId));
        redisForResoure.delete(Constants.CACHE_DEBUG_CONTEXT,debugId);
    }

    /**
     * 请求开始时，初始化本地线程
     * 判断debugId是否存在，如果不存在则创建一个新的debug上下文，同时从redis中获取需要调试的资源，并初始化
     */
    void startThreadLocal(String debugId){

        // 从缓存中获取上下文描述
        ContextDescribe debugContext = redisForContext
                .getIfAbsent(Constants.CACHE_DEBUG_CONTEXT, debugId,() -> null);

        // 如果上下文不存在 抛出错误
        if(debugContext==null){
            throw new TfmsException("当前调试上下文不存在");
        }

        // 如果上下文的mac地址不一致 抛出错误
        if(!StringUtils.equals(OsUtils.getMacAddress(),debugContext.getMac())){
            throw new TfmsException("当前调试上下文与运行环境不一致");
        }

        // 检查Spring Application 是否已经启动，如果没有，则启动，并初始化debug对象
        if(!debugApplications.containsKey(debugId)){

            // 创建上下文
            AnnotationConfigApplicationContext debugApplicationContext = new AnnotationConfigApplicationContext();
            debugApplicationContext.setParent(rootApplicationContext);
            debugApplicationContext.refresh();

            debugApplications.put(debugId,debugApplicationContext);

            // 初始化debug对象
            Optional.ofNullable(redisForResoure.getHashIfAbsent(String.format("DEBUG:%s", debugId), () -> null))
                    .ifPresent((map)->
                        map.entrySet()
                                .stream()
                                .filter(e->e.getKey().startsWith("$"))
                                .map(Map.Entry::getValue)
                                .forEach(e->registerScriptObject((ScriptDefHV) e,debugApplicationContext))
                    );
        }

        // 设置本地线程
        localDebugId.set(debugId);
    }

    /**
     * 请求结束后，清理本地线程
     */
    void exitThreadLocal(){
        localDebugId.remove();
    }

    /**
     * 向debug缓存中添加一个需要调试的资源，如：
     * 前缀 $：脚本Vue对象
     * 前缀 @：单据DocType配置
     */
    public void addDebugResource(String key, Object resource){

        ApplicationContext debugApplicationContext = Optional.ofNullable(localDebugId.get())
                .map(debugApplications::get)
                .orElseThrow(()->new TfmsException("没有找到调试上下文"));

        if(resource instanceof ScriptDefHV){
            ((ScriptDefHV) resource).setDebug(true);
            registerScriptObject((ScriptDefHV) resource,debugApplicationContext);
        }else if(resource instanceof DocDefHV){
            ((DocDefHV) resource).setDebug(true);
        }else{
            throw new TfmsException(resource.getClass().getName() + " 不支持调试");
        }

        redisForResoure.putHash(String.format("DEBUG:%s", localDebugId.get()), key, resource);
    }

    public void addActiveResource(ScriptDefHV scriptDef){
        scriptDef.setDebug(false);
        registerScriptObject(scriptDef, rootApplicationContext);
    }

    private void registerScriptObject(ScriptDefHV scriptDef,ApplicationContext context){

        Class<?> clazz = GroovyUtils.compileGroovy(scriptDef.getScriptName(), scriptDef.getGroovyMain());

        if(!ClassUtils.getAllInterfaces(clazz).contains(NkCustomScriptObject.class)){
            throw new RuntimeException("组件处理程序必须实现[ "+ NkCustomScriptObject.class.getSimpleName()+" ]");
        }

        String beanName = cn.nkpro.ts5.utils.ClassUtils.decapitateBeanName(clazz);

        // 避免非法重写spring的类
        if(context.containsBean(beanName)){

            Object exists = context.getBean(beanName);

            if(!(exists instanceof GroovyObject)){
                if(exists instanceof NkCustomObject){
                    if(((NkCustomObject) exists).isFinal()){
                        throw new RuntimeException(String.format("%s 不支持重写",exists.getClass().getName()));
                    }
                }else{
                    throw new RuntimeException(String.format("%s 不支持重写",exists.getClass().getName()));
                }
            }
        }

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        beanDefinitionBuilder.addPropertyValue("scriptDef", scriptDef);
        ((BeanDefinitionRegistry) context.getAutowireCapableBeanFactory())
                .registerBeanDefinition(beanName,beanDefinitionBuilder.getBeanDefinition());
    }

    /**
     * 获取当前debug的缓存中获取单个资源
     */
    @SuppressWarnings("all")
    public <T> T getDebugResources(String key){
        return (T) redisForResoure.getIfAbsent(String.format("DEBUG:%s", localDebugId.get()),key,()->null);
    }

    /**
     * 获取当前debug的上下文对象
     */
    public ApplicationContext getApplicationContext() {
        return Optional.ofNullable(localDebugId.get())
                .map(debugApplications::get)
                .orElse(rootApplicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.rootApplicationContext = applicationContext;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class ContextDescribe {
        private String id;
        private String mac;
        private String createUser;
        private String contextDesc;
        private boolean visible;
    }
}
