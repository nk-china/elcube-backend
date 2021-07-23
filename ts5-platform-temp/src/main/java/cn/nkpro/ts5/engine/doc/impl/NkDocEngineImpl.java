package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.basic.NKCustomObjectManager;
import cn.nkpro.ts5.engine.doc.NKCard;
import cn.nkpro.ts5.engine.doc.NKDocDefManager;
import cn.nkpro.ts5.engine.doc.ThreadLocalContextHolder;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.model.mb.gen.*;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.ts5.utils.BeanUtilz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class NkDocEngineImpl {

    @Autowired
    private DocHMapper docHMapper;
    @Autowired
    private DocIMapper docIMapper;
    @Autowired
    private RedisSupport<DocHV> redisSupport;
    @Autowired
    private RedisSupport<Object> redisSupportItem;
    @Autowired
    private NKCustomObjectManager customObjectManager;
    @Autowired
    private NKDocDefManager docDefManager;

    public DocHV toCreate(String docType){
        return null;
    }

    public DocHV getDoc(String docId){

        // 获取单据抬头
        DocHV docHV = redisSupport.getIfAbsent(docId, StringUtils.EMPTY,()->{
            return BeanUtilz.copyFromObject(docHMapper.selectByPrimaryKey(docId), DocHV.class);
        });

        // 获取单据DEF
        DocDefHV def = docDefManager.getDef(docHV.getDocType(), docHV.getDefVersion()+"");

        // 获取单据行项目
        doInComponents(docId,def,false,(nkCard,docDefI)->{

            Object itemData = redisSupportItem.getIfAbsent(docId, docDefI.getCardKey(), () -> {

                // 从DB获取卡片数据
                DocIKey docIKey = new DocIKey();
                docIKey.setDocId(docId);
                docIKey.setItemKey(docDefI.getCardKey());
                DocI docI = docIMapper.selectByPrimaryKey(docIKey);

                // 调用卡片程序解析数据
                try{
                    return nkCard.afterGetData(docHV, docI.getItemContent(), docDefI.getCardContent());
                }finally {
                    docI.setItemContent(null);
                }
            });

            docHV.getData().put(docDefI.getCardKey(),itemData);
        });

        // 触发单据数据加载完成事件
        //doInComponents(docId,def,false,(nkCard,docDefI)->{
        //    nkDoc.getData().put(docDefI.getItemKey(),nkDoc.getData().get(docDefI.getItemKey()));
        //});


        return docHV;
    }

    public DocHV doCalc(DocHV doc){
        return null;
    }

    public void doUpdate(DocHV doc){
    }


    private void doInComponents(String docId, DocDefHV defDocTypeBO, boolean loop, RunInComponents runInComponents) {
        /*
         * 为了避免在组件中访问单据方法，引起循环调用，在执行组件方法前，进行单据锁定
         */
        ThreadLocalContextHolder.lockBizDoc(docId);
        try{
            List<DocDefIWithBLOBs> collect = defDocTypeBO.getCards()
                    .stream()
                    .sorted(Comparator.comparingInt(c -> (c.getCalcOrder() == null ? -1 : c.getCalcOrder())))
                    .collect(Collectors.toList());
            int times = 1;
            do{
                boolean retry = false;

                log.info("第"+times+"次计算");

                for(DocDefIWithBLOBs defDocComponent : collect){

                    int timesCalc = defDocComponent.getCalcTimes()!=null ? defDocComponent.getCalcTimes() : 1;
                    if(timesCalc>=times){
                        // 找到对应的组件实现类
                        runInComponents.run(
                                customObjectManager.getCustomObject(defDocComponent.getCardHandler(), NKCard.class),
                                defDocComponent);
                    }

                    retry = retry || timesCalc > times;
                }

                loop = loop && retry;

                times++;
            }while (loop);

        }finally {
            ThreadLocalContextHolder.unlockBizDoc(docId);
        }
    }

    @FunctionalInterface
    private interface RunInComponents{
        void run(NKCard nkCard, DocDefIWithBLOBs docDefI);
    }
}
