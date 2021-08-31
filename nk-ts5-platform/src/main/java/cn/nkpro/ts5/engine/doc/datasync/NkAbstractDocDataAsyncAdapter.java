package cn.nkpro.ts5.engine.doc.datasync;

import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.engine.LocalSyncUtilz;
import cn.nkpro.ts5.engine.spel.TfmsSpELManager;
import cn.nkpro.ts5.orm.mb.gen.DocDefDataSync;
import cn.nkpro.ts5.orm.mb.gen.NkAsyncQueue;
import cn.nkpro.ts5.orm.mb.gen.NkAsyncQueueMapper;
import cn.nkpro.ts5.orm.mb.gen.NkAsyncQueueWithBLOBs;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings({"all"})
@Slf4j
public abstract class NkAbstractDocDataAsyncAdapter<K> extends NkAbstractDocDataSupport implements NkDocDataAsyncAdapter {


    @Autowired
    private RedisSupport<String> redisSupport;
    @Autowired
    protected TfmsSpELManager spELManager;
    @Autowired
    private NkAsyncQueueMapper asyncQueueMapper;

    protected void doSync(Object dataUnmapping, Object dataOriginalUnmapping, EvaluationContext context1, EvaluationContext context2, DocDefDataSync def){

        NkAsyncData asyncData = new NkAsyncData();
        if(dataUnmapping instanceof List){
            asyncData.data = (List) ((List<Map<String,Object>>) dataUnmapping).stream()
                    .map(value -> {
                        context1.setVariable("row", value);
                        return (Map<String, Object>) (spELManager.invoke(def.getMappingSpEL(), context1));
                    }).collect(Collectors.toList());
            asyncData.original = (List<Map<String,Object>>) ((List) dataOriginalUnmapping).stream()
                    .map(value -> {
                        context2.setVariable("row", value);
                        return (Map<String, Object>) (spELManager.invoke(def.getMappingSpEL(), context2));
                    }).collect(Collectors.toList());
        }else{
            context1.setVariable("row", dataUnmapping);
            asyncData.single = true;
            asyncData.data = (Map<String,Object>)(spELManager.invoke(def.getMappingSpEL(), context1));
            asyncData.original = (Map<String,Object>)(spELManager.invoke(def.getMappingSpEL(), context2));
        }

        NkAsyncQueueWithBLOBs queue = new NkAsyncQueueWithBLOBs();
        queue.setAsyncId(UUID.randomUUID().toString());
        queue.setAsyncObjectRef(this.getBeanName());
        queue.setAsyncJson(JSON.toJSONString(asyncData));
        queue.setAsyncRetry(0);
        queue.setAsyncLimit(limit());
        queue.setAsyncRule(rule());
        queue.setCreatedTime(DateTimeUtilz.nowSeconds());
        queue.setUpdatedTime(queue.getCreatedTime());
        asyncQueueMapper.insert(queue);

        LocalSyncUtilz.runAfterCommitLast(()->{
            new RunInner(queue).start();
        });
    }

    class RunInner extends Thread{

        private NkAsyncQueue asyncQueue;

        private RunInner(NkAsyncQueue asyncId){
            this.asyncQueue = asyncQueue;
        }

        @Override
        public void run() {
            NkAbstractDocDataAsyncAdapter.this.schedule(this.asyncQueue);
        }
    }

    @Async
    @Override
    public void run(NkAsyncQueue asyncQueue) {

        long now = DateTimeUtilz.nowSeconds();

        // 如果超出重试次数
        if(asyncQueue.getAsyncRetry() >= asyncQueue.getAsyncLimit()){
            markError(asyncQueue, now);
            return;
        }

        // 如果超出重试间隔设定
        String[] split = asyncQueue.getAsyncRule().split("[,;|\\s]");
        if(split.length<=asyncQueue.getAsyncRetry()){
            markError(asyncQueue, now);
            return;
        }

        // 如果未到重试时间
        long next = asyncQueue.getUpdatedTime() + Integer.parseInt(split[asyncQueue.getAsyncRetry()])*60;
        if(next>now) {
            // 等待
            return;
        }

        // 锁定任务
        if(redisSupport.lock(asyncQueue.getAsyncId(),Thread.currentThread().getName(),60)){
            NkAsyncQueueWithBLOBs asyncQueueWithBLOBs = asyncQueueMapper.selectByPrimaryKey(asyncQueue.getAsyncId());
            try{
                this.schedule(asyncQueueWithBLOBs);
                asyncQueueMapper.deleteByPrimaryKey(asyncQueueWithBLOBs.getAsyncId());
            }catch (Exception e){
                int retry = asyncQueueWithBLOBs.getAsyncRetry()+1;

                if(retry>=asyncQueueWithBLOBs.getAsyncLimit()){
                    markError(asyncQueueWithBLOBs, now, ExceptionUtils.getRootCauseStackTrace(e));
                }else{
                    asyncQueueWithBLOBs.setAsyncRetry(asyncQueueWithBLOBs.getAsyncRetry()+1);
                    asyncQueueWithBLOBs.setUpdatedTime(now);
                    asyncQueueWithBLOBs.setAsyncCauseStackTrace(JSON.toJSONString(ExceptionUtils.getRootCauseStackTrace(e)));
                    asyncQueueMapper.updateByPrimaryKeyWithBLOBs(asyncQueueWithBLOBs);
                }
            }finally {
                redisSupport.unLock(asyncQueue.getAsyncId(),Thread.currentThread().getName());
            }
        }
    }


    private void markError(NkAsyncQueue asyncQueue,long time){
        asyncQueue.setUpdatedTime(time);
        asyncQueue.setAsyncState("ERROR");
        asyncQueueMapper.updateByPrimaryKey(asyncQueue);
    }
    private void markError(NkAsyncQueueWithBLOBs asyncQueue,long time,String[] causeStackTrace){
        asyncQueue.setUpdatedTime(time);
        asyncQueue.setAsyncState("ERROR");
        asyncQueue.setAsyncCauseStackTrace(causeStackTrace!=null?JSON.toJSONString(causeStackTrace):null);
        asyncQueueMapper.updateByPrimaryKeySelective(asyncQueue);
    }

    protected abstract void schedule(NkAsyncQueue asyncQueue);
    protected int limit(){
        return 10;
    };
    protected String rule(){
        return "1 1 1 2 2 5 10 30 60 120";
    };

    @Data
    public static class NkAsyncData{
        private DocDefDataSync def;
        private boolean single;
        private Object data;
        private Object original;
    }
}