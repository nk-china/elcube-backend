package cn.nkpro.elcube.docengine;

import cn.nkpro.elcube.basic.TransactionSync;
import cn.nkpro.elcube.data.redis.RedisSupport;
import cn.nkpro.elcube.docengine.gen.DocH;
import cn.nkpro.elcube.exception.NkOperateNotAllowedCaution;
import cn.nkpro.elcube.exception.NkSystemException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class DocLockAspect {

    @Autowired
    private RedisSupport redisSupport;

    @Pointcut(
        "   execution(public * cn.nkpro.elcube.docengine.NkDocEngine.doUpdate(..)) " +
            "|| execution(public * cn.nkpro.elcube.docengine.service.NkDocEngineFrontService.doUpdateView(..)) " +
            "|| execution(public * cn.nkpro.elcube.docengine.service.NkDocEngineFrontService.onBpmKilled(..)) "
    )
    public void funDocEngineEntrance(){}

    @Around("funDocEngineEntrance()")
    public Object process(ProceedingJoinPoint point) throws Throwable {

        Object arg0 = point.getArgs()[0];

        String docId = null;
        if(arg0 instanceof String){
            docId = (String) arg0;
        }else if(arg0 instanceof DocH){
            docId = ((DocH) arg0).getDocId();
        }


        String lockedValue;

        // 尝试锁定单据1小时
        int i = 0;
        do{
            lockedValue = redisSupport.lock(docId, 3600);

            if(lockedValue!=null)
                break;

            log.debug("尝试获取锁[{}]失败，{}ms后重试", docId, 1000);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new NkSystemException(e.getMessage(),e);
            }
        }while (++i <= 10);

        // 获取锁失败
        if(lockedValue==null){
            log.warn("单据{} 被其他用户锁定，请稍后再试", docId);
            throw new NkOperateNotAllowedCaution("单据被其他用户锁定，请稍后再试");
        }

        // 获取锁成功
        try{
            return point.proceed(point.getArgs());
        }finally {
            String finalDocId = docId;
            String finalLockedValue = lockedValue;
            TransactionSync.runAfterCompletionLast("解除Redis锁",(status)->
                redisSupport.unlock(finalDocId, finalLockedValue)
            );
        }
    }
}
