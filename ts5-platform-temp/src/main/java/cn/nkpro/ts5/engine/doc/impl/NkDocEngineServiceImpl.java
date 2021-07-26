package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.basic.NKCustomObjectManager;
import cn.nkpro.ts5.engine.doc.NKDocProcessor;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocHD;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.doc.service.NKDocDefService;
import cn.nkpro.ts5.engine.doc.service.NkDocEngineFrontService;
import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.orm.mb.gen.*;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.LocalSyncUtilz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NkDocEngineServiceImpl implements NkDocEngineFrontService {

    @Autowired@SuppressWarnings("all")
    private DocHMapper docHMapper;
    @Autowired@SuppressWarnings("all")
    private DocIMapper docIMapper;
    @Autowired@SuppressWarnings("all")
    private RedisSupport<DocHV> redisSupport;
    @Autowired@SuppressWarnings("all")
    private NKCustomObjectManager customObjectManager;
    @Autowired@SuppressWarnings("all")
    private NKDocDefService docDefService;

    @Override
    public DocHV create(String docType, String preDocId) throws Exception {

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

    @Override
    public DocHV detail(String docId) throws Exception {

        // 获取单据抬头和行项目数据
        DocHD docHD = redisSupport.getIfAbsent(Constants.CACHE_DOC, docId,()->{

            DocHV doc = BeanUtilz.copyFromObject(docHMapper.selectByPrimaryKey(docId), DocHV.class);

            if(doc!=null){

                DocIExample example = new DocIExample();
                example.createCriteria()
                        .andDocIdEqualTo(docId);

                doc.setItems(docIMapper.selectByExampleWithBLOBs(example).stream()
                        .collect(Collectors.toMap(DocIKey::getCardKey, e -> e)));
            }

            return doc;
        });

        // 处理数据
        if(docHD != null){

            // 获取单据DEF
            DocDefHV def = docDefService.getDocDef(docHD.getDocType(), docHD.getDefVersion());

            // 获取单据处理器 并执行
            return customObjectManager
                    .getCustomObject(def.getRefObjectType(), NKDocProcessor.class)
                    .detail(def, docHD);
        }

        return null;
    }

    /**
     *
     * @throws IllegalTransactionStateException 在无transaction状态下执行；如果当前已有transaction，则抛出异常
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public DocHV calculate(DocHV doc, String fromCard, String options) throws Exception {

        validate(doc);

        // 获取原始单据数据
        DocHV original = detail(doc.getDocId());

        // 获取单据配置
        DocDefHV def = Optional.ofNullable(original).map(DocHV::getDef).orElseGet(()->
                docDefService.getDocDef(doc.getDocType(),doc.getDefVersion())
        );

        // 获取单据处理器 并执行
        return customObjectManager
                .getCustomObject(def.getRefObjectType(), NKDocProcessor.class)
                .calculate(doc, fromCard, options);
    }

    @Override
    @Transactional
    public DocHV doUpdate(DocHV doc) throws Exception {

        validate(doc);

        // 获取原始单据数据
        DocHV original = detail(doc.getDocId());

        // 获取单据配置
        DocDefHV def = Optional.ofNullable(original).map(DocHV::getDef).orElseGet(()->
                docDefService.getDocDef(doc.getDocType(),doc.getDefVersion())
        );

        // 事务提交后清空缓存
        LocalSyncUtilz.runAfterCommit(()->
                redisSupport.delete(Constants.CACHE_DOC, doc.getDocId())
        );

        // 获取单据处理器 并执行
        return customObjectManager
                .getCustomObject(def.getRefObjectType(), NKDocProcessor.class)
                .doUpdate(def,doc,original,"用户操作");
    }

    private void validate(DocHV doc){
        Assert.hasText(doc.getDocId(),"单据ID不能为空");
        Assert.hasText(doc.getDocType(),"单据类型不能为空");
    }
}
