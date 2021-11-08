//package cn.nkpro.ts5.dataengine;
//
//import cn.nkpro.ts5.data.redis.RedisSupport;
//import cn.nkpro.ts5.dataengine.cards.NkCardDataMapReduce;
//import cn.nkpro.ts5.dataengine.cards.ReduceConfig;
//import cn.nkpro.ts5.dataengine.cards.ReduceState;
//import cn.nkpro.ts5.docengine.NkDocEngine;
//import cn.nkpro.ts5.docengine.gen.DocH;
//import cn.nkpro.ts5.docengine.gen.DocHExample;
//import cn.nkpro.ts5.docengine.gen.DocHMapper;
//import cn.nkpro.ts5.docengine.model.DocHV;
//import cn.nkpro.ts5.security.NkSecurityRunner;
//import cn.nkpro.ts5.security.SecurityUtilz;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.core.task.TaskExecutor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Objects;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//
//@Slf4j
//@Service
//public class NkDataAsyncScheduled {
//
//    @Autowired
//    private DocHMapper docHMapper;
//    @Autowired
//    private NkDocEngine docEngine;
//    @Autowired
//    @Qualifier("nkTaskExecutor")
//    private TaskExecutor taskExecutor;
//    @Autowired
//    private RedisSupport redisSupport;
//    @Autowired
//    private NkSecurityRunner securityRunner;
//
//    @Scheduled(cron = "0/5 * * * * ?")
//    public void cron(){
//
//        securityRunner.runAsUser("admin");
//
//        DocHExample example = new DocHExample();
//        example.createCriteria().andDocTypeEqualTo("D001")
//                .andDocStateEqualTo("S002");
//
//        example.setOrderByClause("CREATED_TIME");
//
//        List<DocH> docs = docHMapper.selectByExample(example);
//
//        if(log.isInfoEnabled())
//            log.info("数据同步重试 任务数量：{}",docs.size());
//
//        docs.forEach(doc->{
//
//            String lockKey = "NkDataAsyncScheduled:"+doc.getDocId();
//            String random = UUID.randomUUID().toString();
//            try{
//
//                redisSupport.lock(lockKey, random,1);
//
//                DocHV docHV = docEngine.detail(doc.getDocId());
//
//                docHV.getDef().getCards()
//                        .stream()
//                        .filter(card -> Objects.equals(NkCardDataMapReduce.class.getSimpleName(), card.getBeanName()))
//                        .filter(card -> {
//
//                            ReduceState reduceState = (ReduceState) docHV.getData().get(card.getCardKey());
//
//                            if (!"Waiting".equals(reduceState.getState())) {
//                                return false;
//                            }
//
//                            ReduceConfig config = (ReduceConfig) card.getConfig();
//
//                            if (StringUtils.isBlank(config.getPreTask())) {
//                                return true;
//                            }
//
//                            ReduceState preReduceState = (ReduceState) docHV.getData().get(config.getPreTask());
//                            return preReduceState != null && "Complete".equals(preReduceState.getState());
//                        })
//                        .map(card -> {
//                            ReduceState reduceState = (ReduceState) docHV.getData().get(card.getCardKey());
//                            reduceState.setState("Running");
//
//                            ReduceConfig config = (ReduceConfig) card.getConfig();
//                            return (Runnable) () -> {
//                                System.out.println(config.getDataCommand());
//                            };
//                        })
//                        .collect(Collectors.toList())
//                        .forEach(runnable -> taskExecutor.execute(runnable));
//
//                docEngine.doUpdate(docHV, "by data job");
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }finally {
//                redisSupport.unLock(lockKey, random);
//            }
//        });
//    }
//}
