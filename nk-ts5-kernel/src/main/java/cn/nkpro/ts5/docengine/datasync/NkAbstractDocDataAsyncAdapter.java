package cn.nkpro.ts5.docengine.datasync;

import cn.nkpro.ts5.basic.Keep;
import cn.nkpro.ts5.basic.TransactionSync;
import cn.nkpro.ts5.data.redis.RedisSupport;
import cn.nkpro.ts5.docengine.gen.DocAsyncQueue;
import cn.nkpro.ts5.docengine.gen.DocAsyncQueueMapper;
import cn.nkpro.ts5.docengine.gen.DocAsyncQueueWithBLOBs;
import cn.nkpro.ts5.docengine.gen.DocDefDataSync;
import cn.nkpro.ts5.co.spel.NkSpELManager;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.expression.EvaluationContext;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings({"all"})
public abstract class NkAbstractDocDataAsyncAdapter<K> extends NkAbstractDocDataSupport implements NkDocDataAsyncAdapter {


    @Autowired
    private RedisSupport<String> redisSupport;
    @Autowired
    protected NkSpELManager spELManager;
    @Autowired
    private DocAsyncQueueMapper asyncQueueMapper;
    @Autowired
    @Qualifier("nkTaskExecutor")
    private TaskExecutor taskExecutor;

    @SuppressWarnings({"all"})
    protected void doSync(Object dataUnmapping, Object dataOriginalUnmapping, EvaluationContext context1, EvaluationContext context2, DocDefDataSync def){

        NkAsyncData asyncData = new NkAsyncData();
        if(dataUnmapping instanceof List){
            asyncData.data = (List) ((List<Map<String,Object>>) dataUnmapping).stream()
                    .map(value -> {
                        context1.setVariable("row", value);
                        return (Map<String, Object>) (spELManager.invoke(def.getMappingSpEL(), context1));
                    }).collect(Collectors.toList());

            if(dataOriginalUnmapping != null) {
                asyncData.original = (List<Map<String, Object>>) ((List) dataOriginalUnmapping).stream()
                        .map(value -> {
                            context2.setVariable("row", value);
                            return (Map<String, Object>) (spELManager.invoke(def.getMappingSpEL(), context2));
                        }).collect(Collectors.toList());
            }
        }else{
            asyncData.single = true;
            context1.setVariable("row", dataUnmapping);
            asyncData.data = (Map<String,Object>)(spELManager.invoke(def.getMappingSpEL(), context1));

            if(dataOriginalUnmapping != null){
                context2.setVariable("row", dataOriginalUnmapping);
                asyncData.original = (Map<String,Object>)(spELManager.invoke(def.getMappingSpEL(), context2));
            }
        }

        DocAsyncQueueWithBLOBs queue = new DocAsyncQueueWithBLOBs();
        queue.setAsyncId(UUID.randomUUID().toString());
        queue.setAsyncObjectRef(this.getBeanName());
        queue.setAsyncJson(JSON.toJSONString(asyncData));
        queue.setAsyncRetry(0);
        queue.setAsyncLimit(limit());
        queue.setAsyncRule(rule());
        queue.setAsyncState("WAITING");
        queue.setCreatedTime(DateTimeUtilz.nowSeconds());
        queue.setUpdatedTime(queue.getCreatedTime());
        asyncQueueMapper.insert(queue);

        TransactionSync.runAfterCommitLast(()->{
            taskExecutor.execute(new RunInner(queue));
        });
    }

    class RunInner extends Thread{

        private DocAsyncQueue asyncQueue;

        private RunInner(DocAsyncQueue asyncQueue){
            this.asyncQueue = asyncQueue;
        }

        @Override
        public void run() {
            NkAbstractDocDataAsyncAdapter.this.lockRun(this.asyncQueue, DateTimeUtilz.nowSeconds());
        }
    }

    @Async("nkTaskExecutor")
    @Override
    public void run(DocAsyncQueue asyncQueue) {

        long now = DateTimeUtilz.nowSeconds();

        // 如果超出重试次数
        if(asyncQueue.getAsyncRetry() >= asyncQueue.getAsyncLimit()){
            markFaild(asyncQueue, now);
            if(log.isDebugEnabled())
            log.debug("任务{} 重试次数 {} 超出限制 {}, 标记为 失败",
                    asyncQueue.getAsyncId(),asyncQueue.getAsyncRetry(),asyncQueue.getAsyncLimit());
            return;
        }

        // 如果超出重试间隔设定
        String[] split = asyncQueue.getAsyncRule().split("[,;|\\s]");
        if(split.length<=asyncQueue.getAsyncRetry()){
            markFaild(asyncQueue, now);
            if(log.isDebugEnabled())
            log.debug("任务{} 重试次数 {} 超出间隔设定 {}, 标记为 失败",
                    asyncQueue.getAsyncId(),asyncQueue.getAsyncRetry(),asyncQueue.getAsyncRule());
            return;
        }

        // 如果未到重试时间
        long next = asyncQueue.getUpdatedTime() + Integer.parseInt(split[asyncQueue.getAsyncRetry()])*60;
        if(next>now) {
            // 等待
            if(log.isDebugEnabled())
            log.debug("任务{} 未到达下一次重试时间 {} 等待下一次重试",
                    asyncQueue.getAsyncId(),DateTimeUtilz.format(next,"MM-dd HH:mm:ss"));
            return;
        }

        lockRun(asyncQueue, now);

    }

    private void lockRun(DocAsyncQueue asyncQueue, long time){
        // 锁定任务
        if(redisSupport.lock(asyncQueue.getAsyncId(),Thread.currentThread().getName(),60)){
            if(log.isDebugEnabled())
                log.debug("锁定任务 {}",asyncQueue.getAsyncId());
            DocAsyncQueueWithBLOBs asyncQueueWithBLOBs = asyncQueueMapper.selectByPrimaryKey(asyncQueue.getAsyncId());
            try{
                this.schedule(asyncQueueWithBLOBs);
                asyncQueueMapper.deleteByPrimaryKey(asyncQueueWithBLOBs.getAsyncId());
            }catch (Exception e){
                int retry = asyncQueueWithBLOBs.getAsyncRetry()+1;

                if(retry>=asyncQueueWithBLOBs.getAsyncLimit()){
                    markFaild(asyncQueueWithBLOBs, time, ExceptionUtils.getRootCauseStackTrace(e));
                    if(log.isWarnEnabled())
                        log.warn("执行任务 {} 失败，重试次数 {} 超出限制 {}, 标记为 失败：{}",
                                asyncQueueWithBLOBs.getAsyncId(),
                                retry,
                                asyncQueueWithBLOBs.getAsyncLimit(),
                                e.getMessage());
                }else{
                    String[] split = asyncQueueWithBLOBs.getAsyncRule().split("[,;|\\s]");
                    if(split.length<=retry){
                        markFaild(asyncQueueWithBLOBs, time, ExceptionUtils.getRootCauseStackTrace(e));
                        if(log.isDebugEnabled())
                            log.debug("执行任务{} 重试次数 {} 超出间隔设定 {}, 标记为 失败：{}",
                                    asyncQueueWithBLOBs.getAsyncId(),
                                    retry,
                                    asyncQueueWithBLOBs.getAsyncRule(),
                                    e.getMessage());
                    }else{
                        long next = time + Integer.parseInt(split[asyncQueueWithBLOBs.getAsyncRetry()])*60;
                        asyncQueueWithBLOBs.setAsyncRetry(retry);
                        asyncQueueWithBLOBs.setAsyncNext(DateTimeUtilz.format(next,"MM-dd HH:mm:ss"));
                        asyncQueueWithBLOBs.setUpdatedTime(time);
                        asyncQueueWithBLOBs.setAsyncCauseStackTrace(JSON.toJSONString(ExceptionUtils.getRootCauseStackTrace(e)));
                        asyncQueueMapper.updateByPrimaryKeyWithBLOBs(asyncQueueWithBLOBs);
                        if(log.isWarnEnabled())
                            log.warn("执行任务 {} 失败，等待{}重试: {}",asyncQueueWithBLOBs.getAsyncId(), asyncQueueWithBLOBs.getAsyncNext(), e.getMessage());
                    }
                }
            }finally {
                redisSupport.unLock(asyncQueueWithBLOBs.getAsyncId(),Thread.currentThread().getName());
                if(log.isDebugEnabled())
                    log.debug("解锁任务 {}",asyncQueueWithBLOBs.getAsyncId());
            }
        }
    }


    private void markFaild(DocAsyncQueue asyncQueue, long time){
        asyncQueue.setUpdatedTime(time);
        asyncQueue.setAsyncState("FAILD");
        asyncQueueMapper.updateByPrimaryKey(asyncQueue);
    }
    private void markFaild(DocAsyncQueueWithBLOBs asyncQueue, long time, String[] causeStackTrace){
        asyncQueue.setUpdatedTime(time);
        asyncQueue.setAsyncState("FAILD");
        asyncQueue.setAsyncCauseStackTrace(causeStackTrace!=null?JSON.toJSONString(causeStackTrace):null);
        asyncQueueMapper.updateByPrimaryKeySelective(asyncQueue);
    }

    @SuppressWarnings({"all"})
    protected abstract void schedule(DocAsyncQueue asyncQueue);
    protected int limit(){
        return 10;
    };
    @SuppressWarnings({"all"})
    protected String rule(){
        return "1 1 1 2 2 5 10 30 60 120";
    };

    @Keep
    @Data
    @SuppressWarnings({"all"})
    public static class NkAsyncData{
        private DocDefDataSync def;
        private boolean single;
        private Object data;
        private Object original;
    }
}