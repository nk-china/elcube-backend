package cn.nkpro.elcube.docengine;

import cn.nkpro.elcube.docengine.gen.DocH;
import cn.nkpro.elcube.docengine.model.DocDefHV;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@Aspect
@Component
public class NkDocEngineThreadLocalAspect {

    private final static ThreadLocal<Boolean>       threadLocalFlag = new ThreadLocal<>();
    private final static ThreadLocal<Stack<String>> threadLocalLogs = new ThreadLocal<>();
    private final static ThreadLocal<List<String>>  threadLocalLock = new ThreadLocal<>();
    private final static ThreadLocal<Map<String, DocDefHV>> threadLocalDocDefs = new ThreadLocal<>();

    @Pointcut(
        "   execution(public * cn.nkpro.elcube.docengine.NkDocEngine.*(..)) " +
        "|| execution(public * cn.nkpro.elcube.docengine.service.NkDocEngineFrontService.*(..)) "
    )
    public void funDocEngineEntrance(){}

    @Around("funDocEngineEntrance()")
    public Object process(ProceedingJoinPoint point) throws Throwable {

        if(threadLocalLogs.get()==null){
            threadLocalLogs.set(new Stack<>());
        }

        String operate = String.format("%-12s",point.getSignature().getName());
        String target  = null;
        switch (point.getSignature().getName()){
            case "detail":
            case "doUpdate":
            case "detailView":
                // docId
                target = (String) point.getArgs()[0];
                break;
            case "create":
            case "createForView":
                // docType
                target = (String) point.getArgs()[0];
                break;
            case "calculate":
            case "random":
            case "doUpdateView":
            case "call":
            case "onBpmKilled":
                // DocH
                target = ((DocH) point.getArgs()[0]).getDocId();
                break;
            default:
                // 其他方法目标不变
        }

        String pop = null;
        if(target!=null){
            pop = operate+':'+target;
            threadLocalLogs.get().add(pop);
            mdc();
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
        }

        // 开始处理线程变量
        boolean isDocEngineEntrance = false;
        if(threadLocalFlag.get()==null){
            threadLocalFlag.set(true);
            isDocEngineEntrance = true;
        }
        try{
            return point.proceed(point.getArgs());
        }finally {

            if(target!=null){
                threadLocalLogs.get().pop();
                log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ");
                mdc();
            }

            if(isDocEngineEntrance){
                threadLocalFlag.remove();
                threadLocalLogs.remove();
                threadLocalLock.remove();
                threadLocalDocDefs.remove();
            }
        }
    }

    private void mdc(){

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i< threadLocalLogs.get().size(); i++){
            builder.append('+');
            builder.append('-');
        }
        if(threadLocalLogs.get().size()>0){
            builder.append(threadLocalLogs.get().peek());
            builder.append(':');
            builder.append(' ');
        }

        MDC.put("placeholder",builder.toString());
    }

    public synchronized DocDefHV localDef(String docType, Function<String, DocDefHV> function){
        Map<String, DocDefHV> docMap = threadLocalDocDefs.get();
        if(docMap==null){
            docMap = new ConcurrentHashMap<>();
            threadLocalDocDefs.set(docMap);
        }

        return docMap.computeIfAbsent(docType, function);
    }

    public synchronized void unlockDoc(String docId){
        List<String> locks = threadLocalLock.get();
        if(locks!=null)
            locks.remove(docId);
    }

    public synchronized void lockDoc(String docId){
        List<String> locks = threadLocalLock.get();
        if(locks==null){
            locks = new Vector<>();
            threadLocalLock.set(locks);
        }
        if(locks.contains(docId)){
            throw new RuntimeException("禁止在组件中对当前单据进行读取与更新操作");
        }
        locks.add(docId);
    }
}
