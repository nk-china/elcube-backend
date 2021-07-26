package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.basic.NKCustomObjectManager;
import cn.nkpro.ts5.engine.doc.NKCard;
import cn.nkpro.ts5.engine.doc.NKDocProcessor;
import cn.nkpro.ts5.engine.doc.ThreadLocalContextHolder;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.doc.service.NKDocDefService;
import cn.nkpro.ts5.engine.doc.service.NkDocEngineFrontService;
import cn.nkpro.ts5.model.mb.gen.DocDefIWithBLOBs;
import cn.nkpro.ts5.model.mb.gen.DocH;
import cn.nkpro.ts5.model.mb.gen.DocHMapper;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.ts5.utils.BeanUtilz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NkDocEngineServiceImpl implements NkDocEngineFrontService {

    @Autowired
    private DocHMapper docHMapper;
    @Autowired
    private RedisSupport<DocHV> redisSupport;
    @Autowired
    private NKCustomObjectManager customObjectManager;
    @Autowired
    private NKDocDefService docDefService;

    @Override
    public DocHV toCreate(String docType, String preDocId) throws Exception {

        // 获取前序单据
        DocHV preDoc = StringUtils.isBlank(preDocId) || StringUtils.equalsIgnoreCase(preDocId,"@") ? null : detail(preDocId);
        Optional<DocHV> optPreDoc = Optional.ofNullable(preDoc);

        // 获取单据配置
        DocDefHV def = docDefService.getDocDef(docType, optPreDoc.map(DocH::getDefVersion).orElse(null));

        // 获取单据处理器
        NKDocProcessor processor = customObjectManager.getCustomObject(def.getRefObjectType(), NKDocProcessor.class);

        // 创建单据
        return processor.toCreate(def, null);
    }

    public DocHV detail(String docId){

        // 获取单据抬头
        DocHV docHV = redisSupport.getIfAbsent(docId, StringUtils.EMPTY,()->{
            return BeanUtilz.copyFromObject(docHMapper.selectByPrimaryKey(docId), DocHV.class);
        });

        // 获取单据DEF
        DocDefHV def = docDefService.getDocDef(docHV.getDocType(), docHV.getDefVersion());

        // 获取单据处理器
        NKDocProcessor processor = customObjectManager.getCustomObject(def.getRefObjectType(), NKDocProcessor.class);

        return processor.detail(def, docId);
//
//        // 获取单据行项目
//        doInComponents(docId,def,false,(nkCard,docDefI)->{
//
//            Object itemData = redisSupportItem.getIfAbsent(docId, docDefI.getCardKey(), () -> {
//
//                // 从DB获取卡片数据
//                DocIKey docIKey = new DocIKey();
//                docIKey.setDocId(docId);
//                docIKey.setItemKey(docDefI.getCardKey());
//                DocI docI = docIMapper.selectByPrimaryKey(docIKey);
//
//                // 调用卡片程序解析数据
//                try{
//                    return nkCard.afterGetData(docHV, docI.getItemContent(), docDefI.getCardContent());
//                }finally {
//                    docI.setItemContent(null);
//                }
//            });
//
//            docHV.getData().put(docDefI.getCardKey(),itemData);
//        });

        // 触发单据数据加载完成事件
        //doInComponents(docId,def,false,(nkCard,docDefI)->{
        //    nkDoc.getData().put(docDefI.getItemKey(),nkDoc.getData().get(docDefI.getItemKey()));
        //});
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
