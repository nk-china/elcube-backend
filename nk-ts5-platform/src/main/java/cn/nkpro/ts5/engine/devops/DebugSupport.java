package cn.nkpro.ts5.engine.devops;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.exception.TfmsException;
import cn.nkpro.ts5.orm.mb.gen.ScriptDefHWithBLOBs;
import cn.nkpro.ts5.utils.ClassUtils;
import cn.nkpro.ts5.utils.GroovyUtils;
import cn.nkpro.ts5.utils.OsUtils;
import cn.nkpro.ts5.config.security.SecurityUtilz;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class DebugSupport implements ApplicationContextAware {
    private final static Map<String, GenericApplicationContext> debugApplications = new ConcurrentHashMap<>();
    private final static ThreadLocal<String>                    localDebugId = new ThreadLocal<>();

    private ApplicationContext rootApplicationContext;

    @Autowired@SuppressWarnings("all")
    private RedisSupport<Object> redisSupport;
    @Autowired@SuppressWarnings("all")
    private RedisSupport<DebugContext> redisSupportDebugContext;

    /**
     * 返回调试上下文列表
     */
    public Collection<DebugContext> getDebugContextList(){
        Map<String, DebugContext> absent = redisSupportDebugContext.getHashIfAbsent(Constants.CACHE_DEBUG_CONTEXT, () -> null);
        if(absent!=null){
            return absent.values().stream()
                    .peek(item-> item.setVisible(StringUtils.equals(item.getMac(),OsUtils.getMacAddress()))).collect(Collectors.toList());
        }
        return null;
    }

    public String startDebugContext(String desc){
        String debugId = UUID.randomUUID().toString();

        // 创建Spring上下文
        GenericApplicationContext context = new GenericApplicationContext(rootApplicationContext);
        context.refresh();
        debugApplications.put(debugId,context);

        // 创建调试上下文
        redisSupportDebugContext.putHash(
                Constants.CACHE_DEBUG_CONTEXT,
                debugId,
                new DebugContext(debugId,OsUtils.getMacAddress(),SecurityUtilz.getUser().getUsername(),desc,true)
        );

        return debugId;
    }

    /**
     * 停止一个上下文
     */
    public void stopDebugContext(String debugId){
        GenericApplicationContext context = debugApplications.remove(debugId);
        if(context!=null){
            context.stop();
        }
        redisSupport.delete(String.format("DEBUG:%s", debugId));
        redisSupport.delete(Constants.CACHE_DEBUG_CONTEXT,debugId);
    }

    /**
     * 请求开始时，初始化本地线程
     * 判断debugId是否存在，如果不存在则创建一个新的debug上下文，同时从redis中获取需要调试的资源，并初始化
     */
    void initThreadLocal(String debugId){
        DebugContext debugContext = redisSupportDebugContext
                .getIfAbsent(Constants.CACHE_DEBUG_CONTEXT, debugId,() -> null);

        if(debugContext==null){
            throw new TfmsException("当前调试上下文不存在");
        }

        if(StringUtils.equals(OsUtils.getMacAddress(),debugContext.getMac())){

            // 如果不是同一台服务器，则不允许
            localDebugId.set(debugId);

            // 检查Spring Application 是否已经启动，如果没有，则启动，并加载debug中的类
            if(!debugApplications.containsKey(debugId)){
                GenericApplicationContext context = new GenericApplicationContext(rootApplicationContext);
                context.refresh();
                debugApplications.put(localDebugId.get(),context);

                getDebugObjectsWithLocalThread("$").forEach(item->{
                    ScriptDefHWithBLOBs scriptDefH = (ScriptDefHWithBLOBs)item;
                    registerDebugClass(
                            GroovyUtils.compileGroovy(scriptDefH.getScriptName(), scriptDefH.getGroovyMain())
                    );
                });
            }
        }else{
            throw new TfmsException("当前调试上下文与运行环境不一致");
        }
    }

    /**
     * 请求结束后，清理本地线程
     */
    void clearThreadLocal(){
        localDebugId.remove();
    }

    /**
     * 向debug缓存中添加一个需要调试的资源，如：
     * 前缀 $：脚本Vue对象
     * 前缀 @：单据DocType配置
     */
    public void setDebugResource(String key, Object resource){

        if(resource instanceof ScriptDefHWithBLOBs){
            ScriptDefHWithBLOBs scriptDefH = (ScriptDefHWithBLOBs) resource;
            registerDebugClass(
                    GroovyUtils.compileGroovy(scriptDefH.getScriptName(), scriptDefH.getGroovyMain())
            );
        }

        String debugCachedHash = String.format("DEBUG:%s", localDebugId.get());
        redisSupport.putHash(debugCachedHash,key,resource);
    }

    /**
     * 向debug上下文中注册一个需要调试的类
     */
    private void registerDebugClass(Class<?> clazz){

        GenericApplicationContext context = (GenericApplicationContext) getDebugApplicationContext();

        ((BeanDefinitionRegistry) context.getAutowireCapableBeanFactory()).registerBeanDefinition(
                ClassUtils.decapitateBeanName(clazz),
                BeanDefinitionBuilder.genericBeanDefinition(clazz).getBeanDefinition()
        );
    }

    /**
     * 获取当前debug的缓存中获取单个资源
     */
    @SuppressWarnings("all")
    public <T> Optional<T> getDebugObjectWithLocalThread(String key){

        return Optional.ofNullable(localDebugId.get())
                .map(debugId -> (T)(redisSupport.getIfAbsent(
                        String.format("DEBUG:%s", localDebugId.get()),
                        key,
                        () -> null))
                );
    }
    /**
     * 获取当前debug的缓存中获取多个资源
     */
    @SuppressWarnings("all")
    public <T> List<T> getDebugObjectsWithLocalThread(String keyPrefix){
        return (List<T>) Optional.ofNullable(localDebugId.get())
                .map(debugId -> {
                    Map<String, Object> objectMap = redisSupport.getHashIfAbsent(String.format("DEBUG:%s", localDebugId.get()), () -> null);
                    if(objectMap==null){
                        return Collections.emptyList();
                    }
                    return objectMap.entrySet()
                            .stream()
                            .filter(e->e.getKey().startsWith(keyPrefix))
                            .map(e->e.getValue())
                            .collect(Collectors.toList());
                }).orElse(Collections.emptyList());
    }

    /**
     * 获取当前debug的上下文对象
     */
    public ApplicationContext getDebugApplicationContext() {
        return Optional.ofNullable(localDebugId.get())
                .map(debugApplications::get)
                .orElse(null);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.rootApplicationContext = applicationContext;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class DebugContext {
        private String id;
        private String mac;
        private String createUser;
        private String contextDesc;
        private boolean visible;
    }
}
