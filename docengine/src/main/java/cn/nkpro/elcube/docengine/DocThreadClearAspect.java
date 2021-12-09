package cn.nkpro.elcube.docengine;

import cn.nkpro.elcube.docengine.gen.DocH;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Slf4j
@Aspect
@Component
public class DocThreadClearAspect {

    private final static ThreadLocal<Boolean>               threadLocalFlag = new ThreadLocal<>();
    private final static ThreadLocal<Stack<String>>         threadLocalLogs = new ThreadLocal<>();

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
            case "onBpmKilled":
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
                // DocH
                target = ((DocH) point.getArgs()[0]).getDocId();
                break;
            default:
                // 其他方法目标不变
        }

        String pop;
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
                log.info("**** 清理本地线程变量");
                threadLocalFlag.remove();
                threadLocalLogs.remove();
                NkDocEngineThreadLocal.threadLocalLock.remove();
                NkDocEngineThreadLocal.threadLocalCurr.remove();
                NkDocEngineThreadLocal.threadLocalDocDefs.remove();
                NkDocEngineThreadLocal.threadLocalDocUpdated.remove();
                log.info("**** 清理本地线程变量完成");
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
}
