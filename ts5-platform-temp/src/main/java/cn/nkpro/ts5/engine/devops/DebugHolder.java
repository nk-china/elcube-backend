package cn.nkpro.ts5.engine.devops;

import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DebugHolder {
    private final static Map<String, ApplicationContext> debugContext = new ConcurrentHashMap<>();
    private final static ThreadLocal<String>             localDebugId = new ThreadLocal<>();

    static void debug(String debugId){
        localDebugId.set(debugId);
    }

    public static String debug(){
        return localDebugId.get();
    }

    static void remove(){
        localDebugId.remove();
    }

    static ApplicationContext getDebugContext() {
        return debugContext.get(localDebugId.get());
    }

    static void setDebugContext(ApplicationContext context){
        debugContext.put(localDebugId.get(),context);
    }
}
