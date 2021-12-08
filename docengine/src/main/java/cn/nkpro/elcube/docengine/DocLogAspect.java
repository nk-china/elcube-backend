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
public class DocLogAspect {

    private ThreadLocal<Stack<String>> threadLocal = new ThreadLocal<>();

    @Pointcut(
        "   execution(public * cn.nkpro.elcube.docengine.NkDocEngine.*(..)) " +
        "|| execution(public * cn.nkpro.elcube.docengine.service.NkDocEngineFrontService.*(..)) "
    )
    public void funDocEngineEntrance(){}

    @Around("funDocEngineEntrance()")
    public Object process(ProceedingJoinPoint point) throws Throwable {

        if(threadLocal.get()==null){
            threadLocal.set(new Stack<>());
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
            threadLocal.get().add(pop);
            mdc();
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
        }
        try{
            return point.proceed(point.getArgs());
        }finally {
            if(target!=null){
                threadLocal.get().pop();
                log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ");
                mdc();
            }
        }
    }

    private void mdc(){

        StringBuilder builder = new StringBuilder();
        for(int i=0;i<threadLocal.get().size();i++){
            builder.append('+');
            builder.append('-');
        }
        if(threadLocal.get().size()>0){
            builder.append(threadLocal.get().peek());
            builder.append(':');
            builder.append(' ');
        }

        MDC.put("placeholder",builder.toString());
    }
}
