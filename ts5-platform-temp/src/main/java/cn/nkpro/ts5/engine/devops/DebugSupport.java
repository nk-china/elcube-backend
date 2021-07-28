package cn.nkpro.ts5.engine.devops;

import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.utils.VersioningUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DebugSupport {
    private final static Map<String, ApplicationContext> debugContext = new ConcurrentHashMap<>();
    private final static ThreadLocal<String>             localDebugId = new ThreadLocal<>();

    @Autowired@SuppressWarnings("all")
    private RedisSupport<Object> redisSupport;

    void setDebugId(String debugId){
        localDebugId.set(debugId);
    }

    void remove(){
        localDebugId.remove();
    }

    ApplicationContext getDebugContext() {
        return debugContext.get(localDebugId.get());
    }

    void setDebugContext(ApplicationContext context){
        debugContext.put(localDebugId.get(),context);
    }

    public void setDebugObject(String key, Object docDefHV){
        String debugCachedHash = String.format("DEBUG:%s", localDebugId.get());
        redisSupport.putHash(debugCachedHash,key,docDefHV);
        redisSupport.expire(debugCachedHash,60*30);//30分钟
    }

    @SuppressWarnings("all")
    public <T> Optional<T> getDebugObject(String key){

        return Optional.ofNullable(localDebugId.get())
                .map(debugId -> (T)(redisSupport.getIfAbsent(
                        String.format("DEBUG:%s", localDebugId.get()),
                        key,
                        () -> null))
                );
    }
}
