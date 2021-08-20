package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.config.security.SecurityUtilz;
import cn.nkpro.ts5.engine.co.NkCustomObjectManager;
import cn.nkpro.ts5.engine.doc.NkDocProcessor;
import cn.nkpro.ts5.engine.doc.interceptor.NkDocFlowInterceptor;
import cn.nkpro.ts5.engine.doc.model.*;
import cn.nkpro.ts5.engine.doc.service.NkDocDefService;
import cn.nkpro.ts5.engine.doc.service.NkDocEngineFrontService;
import cn.nkpro.ts5.engine.doc.service.NkDocPermService;
import cn.nkpro.ts5.engine.task.NkBpmTaskService;
import cn.nkpro.ts5.exception.TfmsDefineException;
import cn.nkpro.ts5.orm.mb.gen.DocHMapper;
import cn.nkpro.ts5.orm.mb.gen.DocIExample;
import cn.nkpro.ts5.orm.mb.gen.DocIKey;
import cn.nkpro.ts5.orm.mb.gen.DocIMapper;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.LocalSyncUtilz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.print.Doc;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NkDocEngineServiceImpl implements NkDocEngineFrontService {

    @Autowired@SuppressWarnings("all")
    private DocHMapper docHMapper;
    @Autowired@SuppressWarnings("all")
    private DocIMapper docIMapper;
    @Autowired@SuppressWarnings("all")
    private RedisSupport<DocHPersistent> redisSupport;
    @Autowired@SuppressWarnings("all")
    private NkCustomObjectManager customObjectManager;
    @Autowired@SuppressWarnings("all")
    private NkDocDefService docDefService;
    @Autowired@SuppressWarnings("all")
    private NkBpmTaskService bpmTaskService;
    @Autowired@SuppressWarnings("all")
    private NkDocPermService docPermService;


    @Override
    public DocHV create(String docType, String preDocId) {

        if(log.isInfoEnabled())log.info("创建单据 docType = {} 检查权限", docType);
        docPermService.assertHasDocPerm(NkDocPermService.MODE_WRITE, docType);

        if(log.isInfoEnabled())log.info("创建单据 docType = {} preDocId = {}",docType, preDocId);

        // 获取前序单据
        DocHV preDoc = StringUtils.isBlank(preDocId) || StringUtils.equalsIgnoreCase(preDocId,"@") ? null : detail(preDocId);

        // 获取单据配置
        DocDefHV def = docDefService.getDocDefForRuntime(docType);

        // 验证业务流
        validateFlow(def,preDoc);

        // 获取单据处理器
        NkDocProcessor processor = customObjectManager.getCustomObject(def.getRefObjectType(), NkDocProcessor.class);

        // 创建单据
        return processor.toCreate(def, preDoc);
    }


    @Override
    public DocHV detailView(String docId) {
        final long start = System.currentTimeMillis();

        if(log.isInfoEnabled())log.info("获取单据视图 docId = {}",docId);
        DocHPersistent docHPersistent = fetchDoc(docId);

        if(log.isInfoEnabled())log.info("获取单据视图 docId = {} 检查权限", docId);
        docPermService.assertHasDocPerm(NkDocPermService.MODE_READ, docId, docHPersistent.getDocType());

        DocHV docHV = ThreadLocalContextHolder.getDoc(docId, (id)-> fetchDocProcess(docHPersistent));

        if(docHV==null)return null;

        if(StringUtils.isNotBlank(docHV.getProcessInstanceId())){
            docHV.setBpmTask(
                    bpmTaskService.taskByBusinessAndAssignee(docId, SecurityUtilz.getUser().getId())
            );
            if(log.isInfoEnabled())log.info("获取单据任务 docId = {}",docId);
        }

        processView(docHV);
        if(log.isInfoEnabled())log.info("获取单据 docId = {}: 完成 总耗时{}ms",docId,System.currentTimeMillis() - start);

        return docHV;
    }

    @Override
    public DocHV detail(String docId) {
        final long start = System.currentTimeMillis();
        if(log.isInfoEnabled())log.info("获取单据 docId = {}",docId);
        DocHV ret = ThreadLocalContextHolder.getDoc(docId, (id)-> fetchDocProcess(fetchDoc(id)));
        if(log.isInfoEnabled())log.info("获取单据 docId = {}: 完成 总耗时{}ms",docId,System.currentTimeMillis() - start);
        return ret;
    }

    private DocHPersistent fetchDoc(String docId){
        if(log.isInfoEnabled())log.info("获取单据 docId = {}: 获取原始数据开始",docId);

        // 获取单据抬头和行项目数据
        final long start = System.currentTimeMillis();
        DocHPersistent docHPersistent = redisSupport.getIfAbsent(Constants.CACHE_DOC, docId,()->{

            DocHPersistent doc = BeanUtilz.copyFromObject(docHMapper.selectByPrimaryKey(docId), DocHV.class);

            if(doc!=null){

                DocIExample example = new DocIExample();
                example.createCriteria()
                        .andDocIdEqualTo(docId);

                doc.setItems(docIMapper.selectByExampleWithBLOBs(example).stream()
                        .collect(Collectors.toMap(DocIKey::getCardKey, e -> e)));
            }

            return doc;
        });
        if(log.isInfoEnabled())log.info("获取单据 docId = {}: 获取原始数据 耗时{}ms", docId, System.currentTimeMillis()-start);

        return  docHPersistent;
    }

    private DocHV fetchDocProcess(DocHPersistent docHPersistent){

        // 处理数据
        if(docHPersistent != null){

            long now = System.currentTimeMillis();

            // 获取单据DEF
            DocDefHV def = docDefService.getDocDefForRuntime(docHPersistent.getDocType());

            // 获取单据处理器 并执行
            NkDocProcessor docProcessor = customObjectManager
                    .getCustomObject(def.getRefObjectType(), NkDocProcessor.class);

            if(log.isInfoEnabled())log.info("获取单据 docId = {}: 获取配置 耗时{}ms", docHPersistent.getDocId(), System.currentTimeMillis()-now);
            if(log.isInfoEnabled())log.info("获取单据 docId = {}: 确定单据处理器 = {}", docHPersistent.getDocId(), docProcessor.getBeanName());

            now = System.currentTimeMillis();
            DocHV detail = docProcessor.detail(def, docHPersistent);
            if(log.isInfoEnabled())log.info("获取单据 docId = {}: 处理单据内容 耗时{}ms", docHPersistent.getDocId(), System.currentTimeMillis()-now);

            return detail;
        }

        return null;
    }

    /**
     *
     * @throws IllegalTransactionStateException 在无transaction状态下执行；如果当前已有transaction，则抛出异常
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public DocHV calculate(DocHV doc, String fromCard, String options) {

        validate(doc);

        // 获取原始单据数据
        DocHV original = detail(doc.getDocId());

        // 获取单据配置
        DocDefHV def = Optional.ofNullable(original).map(DocHV::getDef).orElseGet(()->
                docDefService.getDocDefForRuntime(doc.getDocType())
        );

        // 获取单据处理器 并执行
        return customObjectManager
                .getCustomObject(def.getRefObjectType(), NkDocProcessor.class)
                .calculate(doc, fromCard, options);
    }
    /**
     *
     * @throws IllegalTransactionStateException 在无transaction状态下执行；如果当前已有transaction，则抛出异常
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Object call(DocHV doc, String fromCard, String method, String options) {

        validate(doc);

        // 获取原始单据数据
        DocHV original = detail(doc.getDocId());

        // 获取单据配置
        DocDefHV def = Optional.ofNullable(original).map(DocHV::getDef).orElseGet(()->
                docDefService.getDocDefForRuntime(doc.getDocType())
        );

        // 获取单据处理器 并执行
        return customObjectManager
                .getCustomObject(def.getRefObjectType(), NkDocProcessor.class)
                .call(doc, fromCard, method, options);
    }

    @Override
    @Transactional
    public DocHV doUpdateView(DocHV docHV, String optSource){

        if(log.isInfoEnabled())log.info("保存单据视图 docId = {} 检查权限", docHV.getDocId());
        docPermService.assertHasDocPerm(NkDocPermService.MODE_WRITE, docHV.getDocId(), docHV.getDocType());

        DocHV original = detail(docHV.getDocId());
        if(original!=null){
            original.getData().forEach((k,v)-> docHV.getData().putIfAbsent(k,v));
        }

        DocHV doc = doUpdate(docHV, optSource);

        if(StringUtils.isNotBlank(doc.getProcessInstanceId())){
            doc.setBpmTask(
                    bpmTaskService.taskByBusinessAndAssignee(doc.getDocId(), SecurityUtilz.getUser().getId())
            );
        }
        processView(doc);
        return doc;
    }

    @Override
    @Transactional
    public DocHV doUpdate(DocHV doc, String optSource) {
        final long start = System.currentTimeMillis();
        if(log.isInfoEnabled())log.info("保存单据 docId = {}: 开始",doc.getDocId());

        validate(doc);

        // 获取原始单据数据
        if(log.isInfoEnabled())log.info("保存单据 docId = {}: 准备获取原始单据",doc.getDocId());
        Optional<DocHV> optionalOriginal = Optional.ofNullable(detail(doc.getDocId()));


        // 获取单据配置
        String docType = doc.getDocType();
        DocDefHV def = optionalOriginal.map(DocHV::getDef).orElseGet(()->
                docDefService.getDocDefForRuntime(docType)
        );

        if(optionalOriginal.isPresent()){
            // 修改
            Assert.isTrue(StringUtils.equals(doc.getIdentification(),optionalOriginal.get().getIdentification()),
                    "单据被其他用户修改，请刷新后重试");
        }else{
            // 新建
            validateFlow(def, detail(doc.getPreDocId()));
        }

        long now = System.currentTimeMillis();
        boolean lock = false;
        String  lockId = UUID.randomUUID().toString();

        try{
            lock = redisSupport.lock(doc.getDocId(), lockId, 10);
            Assert.isTrue(lock,"单据被其他用户锁定，请稍后再试");

            if(log.isInfoEnabled())log.info("保存单据 docId = {}: 锁定单据成功 redis",doc.getDocId());

            doc.setDef(def);
            // 获取单据处理器 并执行
            return customObjectManager
                            .getCustomObject(def.getRefObjectType(), NkDocProcessor.class)
                            .doUpdate(doc, optionalOriginal.orElse(null),optSource);
        }finally {
            if(lock){
                redisSupport.unLock(doc.getDocId(), lockId);
                if(log.isInfoEnabled())log.info("保存单据 docId = {}: 解锁单据 redis",doc.getDocId());
            }
            if(log.isInfoEnabled())log.info("保存单据 docId = {}: 保存单据内容 耗时{}ms",doc.getDocId(),System.currentTimeMillis() - now);

            // 事务提交后清空缓存 | 事务提交后重新写入缓存 todo 检查是否有问题
            LocalSyncUtilz.runAfterCommit(()-> {

                //DocHPersistent docHPersistent = BeanUtilz.copyFromObject(doc,DocHPersistent.class);
                //redisSupport.putHash(Constants.CACHE_DOC, doc.getDocId(), docHPersistent);

                redisSupport.delete(Constants.CACHE_DOC, doc.getDocId());
                if(log.isInfoEnabled())log.info("保存单据 docId = {}: 完成 总耗时{}ms",doc.getDocId(),System.currentTimeMillis() - start);
            });
        }
    }

    @Override
    public void onBpmKilled(String docId, String processKey, String optSource) {
        DocHV docHV = detail(docId);
        customObjectManager.getCustomObject(docHV.getDef().getRefObjectType(), NkDocProcessor.class)
                .doOnBpmKilled(docHV, processKey, optSource);
        // 事务提交后清空缓存
        LocalSyncUtilz.runAfterCommit(()-> redisSupport.delete(Constants.CACHE_DOC, docId));
    }


    private void validate(DocHV doc){
        Assert.hasText(doc.getDocId(),"单据ID不能为空");
        Assert.hasText(doc.getDocType(),"单据类型不能为空");
    }


    private NkDocFlowInterceptor.FlowDescribe applyDocFlowInterceptor(String docFlowInterceptor, DocHV docHV){
        return customObjectManager.getCustomObject(docFlowInterceptor, NkDocFlowInterceptor.class)
                .apply(docHV);
    }

    /**
     * 验证业务流是否满足要求
     */
    private void validateFlow(DocDefHV def, DocHV preDoc){

        long now = System.currentTimeMillis();

        String preDocType = Optional.ofNullable(preDoc).map(DocHV::getDocType).orElse("@");
        String preDocState = Optional.ofNullable(preDoc).map(DocHV::getDocState).orElse("@");

        if(log.isInfoEnabled())log.info("验证单据业务流 docType = {}: 验证单据业务流 前序docType = {}",def.getDocType(), preDocType);

        DocDefFlowV flowV = def.getFlows()
                .stream()
                .filter(item -> StringUtils.equals(item.getPreDocType(), preDocType))
                .findFirst()
                .orElseThrow(()->new TfmsDefineException("没有找到业务流配置"));

        if(!StringUtils.equalsAny(flowV.getPreDocState(),preDocState, "@")){
            throw new TfmsDefineException("状态不满足条件");
        }
        if(StringUtils.isNotBlank(flowV.getRefObjectType())){
            NkDocFlowInterceptor.FlowDescribe flowDescribe = applyDocFlowInterceptor(flowV.getRefObjectType(), preDoc);
            if(!flowDescribe.isVisible()){
                throw new TfmsDefineException(flowDescribe.getVisibleDesc());
            }
        }
        if(log.isInfoEnabled())log.info("验证单据业务流 docType = {}: 完成 耗时{}ms", def.getDocType(), System.currentTimeMillis() - now);
    }

    /**
     * 处理可创建的后续单据类型
     */
    private void processView(DocHV docHV){

        docPermService.filterDocCards(null, docHV);

        Map<String, DocDefStateV> cache = new LinkedHashMap<>();
        docHV.getDef()
                .getStatus()
                .forEach(state->{
                    if(StringUtils.equalsAny(docHV.getDocState(),state.getPreDocState(),state.getDocState())){
                        cache.putIfAbsent(state.getDocState(),state);
                    }
                });
        docHV.getDef().setStatus(new ArrayList<>(cache.values()));
        if(log.isInfoEnabled())log.info("设置单据可用状态 docId = {}: 状态 = {}",docHV.getDocId(), docHV.getDef().getStatus());

        docHV.getDef()
            .getNextFlows()
            .forEach(flow->{
                String[] splitState = StringUtils.split(flow.getPreDocState(), ',');
                boolean visibleState = ArrayUtils.contains(splitState,docHV.getDocState()) || ArrayUtils.contains(splitState,"@");

                if(!visibleState){
                    flow.setVisibleDesc("状态不满足条件");
                }

                // 处理 单据业务流的自定义条件
                if(visibleState && StringUtils.isNotBlank(flow.getRefObjectType())){
                    NkDocFlowInterceptor.FlowDescribe flowDescribe = applyDocFlowInterceptor(flow.getRefObjectType(), docHV);
                    if(!flowDescribe.isVisible()){
                        flow.setVisibleDesc(flowDescribe.getVisibleDesc());
                    }
                }

                flow.setVisible(visibleState);
            });
        if(log.isInfoEnabled())log.info("设置单据后续操作 docId = {}: 操作数量 = {}",docHV.getDocId(), docHV.getDef().getNextFlows().size());
    }
}
