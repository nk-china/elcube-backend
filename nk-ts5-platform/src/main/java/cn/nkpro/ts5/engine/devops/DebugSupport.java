package cn.nkpro.ts5.engine.devops;

import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.utils.ClassUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
    private final static Map<String, ApplicationContext> debugContext = new ConcurrentHashMap<>();
    private final static ThreadLocal<String>             localDebugId = new ThreadLocal<>();

    private ApplicationContext rootApplicationContext;

    @Autowired@SuppressWarnings("all")
    private RedisSupport<Object> redisSupport;

    /**
     * 请求开始时，初始化本地线程
     */
    void initThreadLocal(String debugId){
        localDebugId.set(debugId);
    }

    /**
     * 请求结束后，清理本地线程
     */
    void clearThreadLocal(){
        localDebugId.remove();
    }

    /**
     * 向debug上下文中注册一个需要调试的类
     */
    public void setDebugClass(Class<?> clazz){

        ApplicationContext context = getDebugApplicationContext();

        if(context==null){
            context = new GenericApplicationContext(rootApplicationContext);
            ((GenericApplicationContext)context).refresh();
            debugContext.put(localDebugId.get(),context);
        }

        ((BeanDefinitionRegistry) context.getAutowireCapableBeanFactory()).registerBeanDefinition(
                ClassUtils.decapitateBeanName(clazz),
                BeanDefinitionBuilder.genericBeanDefinition(clazz).getBeanDefinition()
        );
    }

    /**
     * 向debug缓存中添加一个需要调试的资源，如：
     * 前缀 $：脚本Vue对象
     * 前缀 @：单据DocType配置
     */
    public void setDebugResource(String key, Object resource){
        String debugCachedHash = String.format("DEBUG:%s", localDebugId.get());
        redisSupport.putHash(debugCachedHash,key,resource);
        redisSupport.expire(debugCachedHash,60*30);//30分钟
    }

    /**
     * 从debug缓存中获取单个资源
     */
    @SuppressWarnings("all")
    public <T> Optional<T> getDebugObject(String key){

        return Optional.ofNullable(localDebugId.get())
                .map(debugId -> (T)(redisSupport.getIfAbsent(
                        String.format("DEBUG:%s", localDebugId.get()),
                        key,
                        () -> null))
                );
    }
    @SuppressWarnings("all")
    public <T> List<T> getDebugObjects(String keyPrefix){
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
        String debugId = localDebugId.get();
        return debugId !=null ? debugContext.get(debugId) : null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.rootApplicationContext = applicationContext;
    }
}
