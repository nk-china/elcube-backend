package cn.nkpro.easis.docengine.service.impl;

import cn.nkpro.easis.basic.Constants;
import cn.nkpro.easis.basic.PageList;
import cn.nkpro.easis.basic.TransactionSync;
import cn.nkpro.easis.co.DebugContextManager;
import cn.nkpro.easis.co.NkCustomObjectManager;
import cn.nkpro.easis.data.elasticearch.SearchEngine;
import cn.nkpro.easis.data.mybatis.pagination.PaginationContext;
import cn.nkpro.easis.data.redis.RedisSupport;
import cn.nkpro.easis.docengine.NkDocEngine;
import cn.nkpro.easis.docengine.NkDocProcessor;
import cn.nkpro.easis.docengine.gen.*;
import cn.nkpro.easis.docengine.interceptor.NkDocFlowInterceptor;
import cn.nkpro.easis.docengine.model.*;
import cn.nkpro.easis.docengine.model.es.DocHES;
import cn.nkpro.easis.docengine.service.NkDocDefService;
import cn.nkpro.easis.docengine.service.NkDocEngineContext;
import cn.nkpro.easis.docengine.service.NkDocEngineFrontService;
import cn.nkpro.easis.docengine.service.NkDocPermService;
import cn.nkpro.easis.exception.NkDefineException;
import cn.nkpro.easis.exception.NkOperateNotAllowedCaution;
import cn.nkpro.easis.exception.abstracts.NkRuntimeException;
import cn.nkpro.easis.security.SecurityUtilz;
import cn.nkpro.easis.task.NkBpmTaskService;
import cn.nkpro.easis.utils.BeanUtilz;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NkDocEngineServiceImpl extends AbstractNkDocEngine implements NkDocEngineFrontService {

    @Autowired@SuppressWarnings("all")
    private DocHMapper docHMapper;
    @Autowired@SuppressWarnings("all")
    private DocIMapper docIMapper;
    @Autowired@SuppressWarnings("all")
    private DocIIndexMapper docIIndexMapper;
    @Autowired@SuppressWarnings("all")
    private RedisSupport<DocHPersistent> redisSupport;
    @Autowired@SuppressWarnings("all")
    private RedisSupport<DocHV> redisRuntime;
    @Autowired@SuppressWarnings("all")
    private NkCustomObjectManager customObjectManager;
    @Autowired@SuppressWarnings("all")
    private NkDocDefService docDefService;
    @Autowired@SuppressWarnings("all")
    private NkBpmTaskService bpmTaskService;
    @Autowired@SuppressWarnings("all")
    private NkDocPermService docPermService;
    @Autowired@SuppressWarnings("all")
    private DebugContextManager debugContextManager;
    @Autowired@SuppressWarnings("all")
    private SearchEngine searchEngine;



    @Override
    public PageList<DocH> list(String docType, int offset, int rows, String orderBy){
        DocHExample example = new DocHExample();
        DocHExample.Criteria criteria = example.createCriteria();

        if(StringUtils.isNotBlank(docType))
            criteria.andDocTypeEqualTo(docType);

        example.setOrderByClause(StringUtils.defaultIfBlank(orderBy,"CREATED_TIME asc"));

        PaginationContext context = PaginationContext.init();

        return new PageList<>(docHMapper.selectByExample(example,new RowBounds(offset,rows)),offset,rows,context.getTotal());
    }

    @Override
    public DocHV create(String docType, String preDocId) {
        NkDocEngineContext.startLog("CREATE", docType +" from "+ StringUtils.defaultIfBlank(preDocId,"@"));
        try{
            if(log.isInfoEnabled())log.info("{}创建单据 开始", NkDocEngineContext.currLog());

            docPermService.assertHasDocPerm(NkDocPermService.MODE_WRITE, docType);

            // 获取前序单据
            DocHV preDoc = StringUtils.isBlank(preDocId) || StringUtils.equalsIgnoreCase(preDocId,"@") ? null : detail(preDocId);

            // 获取单据配置
            DocDefHV def = docDefService.getDocDefForRuntime(docType);

            // 验证业务流
            validateFlow(def,preDoc);

            // 获取单据处理器
            NkDocProcessor processor = customObjectManager.getCustomObject(def.getRefObjectType(), NkDocProcessor.class);

            // 创建单据
            DocHV docHV = processor.toCreate(def, preDoc);
            docHV.setRuntimeKey(UUID.randomUUID().toString());

            return processView(docHV);
        }finally {
            NkDocEngineContext.endLog();
        }
    }


    @Override
    public DocHV detailView(String docId) {
        NkDocEngineContext.startLog("DETAIL", docId);
        try{
            final long start = System.currentTimeMillis();

            if(log.isInfoEnabled()){
                log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                log.info("{}获取单据视图", NkDocEngineContext.currLog());
            }
            // 获取原始数据

            AtomicReference<DocHPersistent> docHPersistent = new AtomicReference<>();
            if(debugContextManager.isDebug()) {
                docHPersistent.set(debugContextManager.getDebugResource("$" + docId));
            }

            if(docHPersistent.get()==null){

                docHPersistent.set(fetchDoc(docId));
                Assert.notNull(docHPersistent.get(),"单据不存在");

                // 检查READ权限
                docPermService.assertHasDocPerm(NkDocPermService.MODE_READ, docId, docHPersistent.get().getDocType());
            }

            DocHV docHV = NkDocEngineContext.getDoc(docId, (id)-> fetchDocProcess(docHPersistent.get()));
            Assert.notNull(docHV,"单据不存在");

            processView(docHV);
            if(log.isInfoEnabled()){
                log.info("{}获取单据视图 完成 总耗时{}ms", NkDocEngineContext.currLog(),System.currentTimeMillis() - start);
                log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
            }
            return docHV;
        }finally {
            NkDocEngineContext.endLog();
        }
    }

    @Override
    public DocHV detail(String docId) {

        final long start = System.currentTimeMillis();
        NkDocEngineContext.startLog("DETAIL", docId);
        try{
            if(log.isInfoEnabled())log.info("{}获取单据", NkDocEngineContext.currLog());
            return NkDocEngineContext.getDoc(docId, (id)-> fetchDocProcess(fetchDoc(id)));
        }finally {
            if(log.isInfoEnabled())log.info("{}获取单据 完成 总耗时{}ms", NkDocEngineContext.currLog(), System.currentTimeMillis() - start);
            NkDocEngineContext.endLog();
        }
    }

    private DocHPersistent fetchDoc(String docId){
        if(log.isInfoEnabled())log.info("{}获取单据原始数据", NkDocEngineContext.currLog());

        // 获取单据抬头和行项目数据
        final long start = System.currentTimeMillis();

        try{
            if(debugContextManager.isDebug()){
                DocHPersistent doc = debugContextManager.getDebugResource("$"+docId);
                if(doc!=null)
                    return doc;

                return fetchDocFromDB(docId);
            }
            return redisSupport.getIfAbsent(Constants.CACHE_DOC, docId,()-> fetchDocFromDB(docId));

        }finally {
            if(log.isInfoEnabled())
                log.info("{}获取单据原始数据 耗时{}ms", NkDocEngineContext.currLog(), System.currentTimeMillis()-start);
        }
    }

    private DocHPersistent fetchDocFromDB(String docId){

        DocHPersistent doc = BeanUtilz.copyFromObject(docHMapper.selectByPrimaryKey(docId), DocHPersistent.class);

        if(doc!=null){

            DocIExample example = new DocIExample();
            example.createCriteria()
                    .andDocIdEqualTo(docId);

            doc.setItems(docIMapper.selectByExampleWithBLOBs(example).stream()
                    .collect(Collectors.toMap(DocIKey::getCardKey, e -> e)));

            DocIIndexExample iIndexExample = new DocIIndexExample();
            iIndexExample.createCriteria()
                    .andDocIdEqualTo(docId);
            iIndexExample.setOrderByClause("ORDER_BY");

            docIIndexMapper.selectByExample(iIndexExample).forEach(docIIndex -> {
                try {
                    doc.getDynamics().put(docIIndex.getName(),JSON.parseObject(docIIndex.getValue(),Class.forName(docIIndex.getDataType())));
                } catch (ClassNotFoundException ex) {
                    doc.getDynamics().put(docIIndex.getName(),null);
                }
            });
        }

        return doc;
    }

    private DocHV fetchDocProcess(DocHPersistent docHPersistent){

        // 处理数据
        if(docHPersistent != null){

            // 获取单据DEF
            DocDefHV def = docDefService.getDocDefForRuntime(docHPersistent.getDocType());

            // 获取单据处理器 并执行
            NkDocProcessor docProcessor = customObjectManager
                    .getCustomObject(def.getRefObjectType(), NkDocProcessor.class);
            if(log.isInfoEnabled())log.info("{}确定单据处理器 = {}", NkDocEngineContext.currLog(),docProcessor.getBeanName());

            return docProcessor.detail(def, docProcessor.deserialize(def, docHPersistent));
        }
        return null;
    }

    /**
     *
     * @throws IllegalTransactionStateException 在无transaction状态下执行；如果当前已有transaction，则抛出异常
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public DocHV calculate(DocHV doc, String fromCard, Object options) {
        NkDocEngineContext.startLog("CALCULATE", doc.getDocId());
        if(log.isInfoEnabled())log.info("{}开始单据计算", NkDocEngineContext.currLog());
        try{

            validate(doc);
            DocDefHV defHV = docDefService.deserializeDef(doc.getDef());

            DocHV runtime = processRuntime(doc);
            /*
             *  todo 替换def
             *  因为前端回传的单据配置是经过权限过滤的，不完整的，所以需要重新获取完整的单据和数据进行计算
             *  计算完成后，再进行权限过滤，返回给前端
             */
            //DocDefHV def = docDefService.getDocDefForRuntime(doc.getDocType());
            //doc.setDef(def);

            // 获取单据处理器 并执行
            doc =  customObjectManager
                    .getCustomObject(runtime.getDef().getRefObjectType(), NkDocProcessor.class)
                    .calculate(doc, fromCard, options);

            doc.setDef(defHV);

            return processView(doc);

        }finally {
            if(log.isInfoEnabled())log.info("{}单据计算 完成", NkDocEngineContext.currLog());
            NkDocEngineContext.endLog();
        }
    }

    private void clearRuntime(DocHV doc){
        if(StringUtils.isNotBlank(doc.getRuntimeKey())){
            redisRuntime.delete(doc.getRuntimeKey());
            doc.setRuntimeKey(null);
        }
    }
    private DocHV processRuntime(DocHV doc){

        // 获取单据运行时状态，如果runtimeKey为空，获取当前最新单据作为运行时
        DocHV docHVRuntime;
        if(StringUtils.isNotBlank(doc.getRuntimeKey())){
            docHVRuntime = redisRuntime.get(doc.getRuntimeKey());

            Assert.notNull(docHVRuntime,"操作已超时");
        }else{
            docHVRuntime = detail(doc.getDocId());
            doc.setRuntimeKey(UUID.randomUUID().toString());
        }

        // 将运行时数据填充到用户单据中，使单据数据完整
        docHVRuntime.getData().forEach((k,v)-> doc.getData().putIfAbsent(k,v));
        docHVRuntime.getDef().getCards().forEach(runtimeCard->{
            boolean present = doc.getDef().getCards().stream()
                    .anyMatch(card -> StringUtils.equals(card.getCardKey(), runtimeCard.getCardKey()));
            if(!present)
                doc.getDef().getCards().add(runtimeCard);
        });
        doc.getDef().getCards().sort(Comparator.comparingInt(DocDefI::getOrderBy));

        return docHVRuntime;
    }

    /**
     *
     * @throws IllegalTransactionStateException 在无transaction状态下执行；如果当前已有transaction，则抛出异常
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Object call(DocHV doc, String fromCard, String method, Object options) {
        NkDocEngineContext.startLog("CALL", doc.getDocId());
        if(log.isInfoEnabled())log.info("{}开始调用单据程序", NkDocEngineContext.currLog());
        try{
            validate(doc);

            // 获取单据配置
            DocDefHV def = docDefService.deserializeDef(doc.getDef());

            // 获取单据处理器 并执行
            return customObjectManager
                    .getCustomObject(def.getRefObjectType(), NkDocProcessor.class)
                    .call(doc, fromCard, method, options);
        }finally {
            if(log.isInfoEnabled())log.info("{}调用单据程序 完成", NkDocEngineContext.currLog());
            NkDocEngineContext.endLog();
        }
    }

    @Override
    @Transactional
    public DocHV doUpdateView(DocHV docHV, String optSource){

        if(debugContextManager.isDebug()){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            System.out.println("\n\n\n");
            System.out.println("当前请求为Debug模式，所有操作的事务不会提交，但对于外部系统接口、redis、es等可能会有不可避免的数据更新，需要谨慎");
            System.out.println("\n\n\n");
        }

        NkDocEngineContext.startLog("UPDATE", docHV.getDocId());

        String      lockId = UUID.randomUUID().toString();
        boolean     lock = redisSupport.lock(docHV.getDocId(), lockId, 10, 1000,600);
        Assert.isTrue(lock,"单据被其他用户锁定，请稍后再试");
        if(log.isInfoEnabled())log.info("{}锁定单据成功 redis", NkDocEngineContext.currLog());

        try {
            if(log.isInfoEnabled())log.info("{}开始保存单据视图",NkDocEngineContext.currLog());

            docPermService.assertHasDocPerm(NkDocPermService.MODE_WRITE, docHV.getDocType());

            if(log.isInfoEnabled())log.info("{}准备获取原始单据 用于填充被权限过滤的数据", NkDocEngineContext.currLog());

            DocDefHV def = docDefService.deserializeDef(docHV.getDef());

            DocHV runtime;
            try{
                runtime = processRuntime(docHV);
            }catch (Exception e){
                redisSupport.unLock(docHV.getDocId(), lockId);
                throw new NkOperateNotAllowedCaution(e.getMessage());
            }

            // 如果单据为修改模式下，检查是否有该单据的write权限
            if(!runtime.isNewCreate()){
                if(!debugContextManager.isDebug()){
                    docPermService.assertHasDocPerm(NkDocPermService.MODE_WRITE, docHV.getDocId(), docHV.getDocType());
                }
            }

//            DocHV original = detail(docHV.getDocId());
//            if(original!=null){
//                original.getData().forEach((k,v)-> docHV.getData().putIfAbsent(k,v));
//            }
            docHV = execUpdate(docHV, optSource, lockId);
            docHV.setDef(def);

            processView(docHV);
            clearRuntime(docHV);

            if(debugContextManager.isDebug()){
                debugContextManager.addDebugResource("$"+docHV.getDocId(), docHV.toPersistent());
            }

            return docHV;
        }finally {
            if(log.isInfoEnabled())log.info("{}保存单据视图 完成",NkDocEngineContext.currLog());
            NkDocEngineContext.endLog();
            docHV.clearItemContent();
        }
    }

    @Override
    @Transactional
    public DocHV doUpdate(String docId, String optSource, NkDocEngine.Function function){
        NkDocEngineContext.startLog("UPDATE Thread safety", docId);
        DocHV docHV = null;
        try{
            String      lockId = UUID.randomUUID().toString();
            boolean     lock = redisSupport.lock(docId, lockId, 10, 1000,600);
            Assert.isTrue(lock,"单据被其他用户锁定，请稍后再试");
            if(log.isInfoEnabled())log.info("{}锁定单据成功 redis", NkDocEngineContext.currLog());

            NkDocEngineContext.clearDoc(docId);

            docHV = detail(docId);
            function.apply(docHV);
            return execUpdate(docHV, optSource, lockId);

        }finally {
            NkDocEngineContext.endLog();
            if(docHV != null){
                docHV.clearItemContent();
            }
        }
    }

    @Override
    @Transactional
    public DocHV doUpdate(DocHV doc, String optSource) {
        NkDocEngineContext.startLog("UPDATE", doc.getDocId());
        try{
            String      lockId = UUID.randomUUID().toString();
            boolean     lock = redisSupport.lock(doc.getDocId(), lockId, 10, 1000,600);
            Assert.isTrue(lock,"单据被其他用户锁定，请稍后再试");
            if(log.isInfoEnabled())log.info("{}锁定单据成功 redis", NkDocEngineContext.currLog());

            // * 锁定单据后，清除本地线程中的单据，保证获取到的数据是最新的
            NkDocEngineContext.clearDoc(doc.getDocId());

            return execUpdate(doc, optSource, lockId);
        }finally {
            NkDocEngineContext.endLog();
            doc.clearItemContent();
        }

    }

    @Override
    public DocHV random(DocHV doc) {
        NkDocEngineContext.startLog("RANDOM", doc.getDocId());
        if(log.isInfoEnabled())log.info("{}开始生成随机数据", NkDocEngineContext.currLog());
        try{

            validate(doc);
            DocDefHV def = docDefService.deserializeDef(doc.getDef());

            // 获取单据处理器 并执行
            return customObjectManager
                    .getCustomObject(def.getRefObjectType(), NkDocProcessor.class)
                    .random(doc);
        }finally {
            if(log.isInfoEnabled())log.info("{}生成随机数据 完成", NkDocEngineContext.currLog());
            NkDocEngineContext.endLog();
        }
    }

    private DocHV execUpdate(DocHV doc, String optSource, String lockId){
        final long      start = System.currentTimeMillis();
        String          docId = doc.getDocId();
        DocHPersistent  docHPersistent = null;

        try{

            if(log.isInfoEnabled())
                log.info("{}开始保存单据", NkDocEngineContext.currLog());

            validate(doc);


            // 获取原始单据数据
            if(log.isInfoEnabled())
                log.info("{}准备获取原始单据 用户对比单据修改前后的差异", NkDocEngineContext.currLog());

            Optional<DocHV> optionalOriginal = Optional.ofNullable(detail(doc.getDocId()));


            // 获取单据配置
            DocDefHV def = docDefService.deserializeDef(doc.getDef());

            if(optionalOriginal.isPresent()){
                // 修改
                // 检查单据数据是否已经发生变化
                Assert.isTrue(StringUtils.equals(doc.getIdentification(),optionalOriginal.get().getIdentification()),
                    "单据被其他用户修改，请刷新后重试");
            }else{
                // 新建
                // 检查单据是否符合业务流控制
                validateFlow(def, detail(doc.getPreDocId()));
            }

            // 获取单据处理器 并执行
            doc = customObjectManager
                    .getCustomObject(def.getRefObjectType(), NkDocProcessor.class)
                    .doUpdate(doc, optionalOriginal.orElse(null),optSource);

            if(log.isInfoEnabled())
                log.info("{}保存单据内容 创建重建index任务", NkDocEngineContext.currLog());

            execDataSync(doc, optionalOriginal.orElse(null));

            // 预创建一个持久化对象，在事务提交后使用
            docHPersistent = doc.toPersistent();

            return doc;
        }finally {
            // 事务提交后清空缓存 | 事务提交后重新写入缓存
            AtomicReference<String>         atomicLog   = new AtomicReference<>(NkDocEngineContext.currLog());
            AtomicReference<DocHPersistent> atomicDocHP = new AtomicReference<>(docHPersistent);

            // tips: 先删除缓存，避免事务提交成功后，缓存更新失败
            redisSupport.deleteHash(Constants.CACHE_DOC, docId);

            TransactionSync.runAfterCompletion((status)-> {

                try{
                    if(status == TransactionSynchronization.STATUS_COMMITTED){
                        // 如果事务更新成功，将更新后的单据更新到缓存
                        redisSupport.set(Constants.CACHE_DOC, docId, atomicDocHP.get());
                        if(log.isInfoEnabled())log.info("{}更新缓存", atomicLog.get());
                    }
                }finally {
                    // 解锁单据
                    redisSupport.unLock(docId, lockId);
                    if(log.isInfoEnabled())log.info("{}解锁单据 redis分布式锁", atomicLog.get());
                }

                if(log.isInfoEnabled())log.info("{}保存单据 完成 总耗时{}ms", atomicLog.get(), System.currentTimeMillis() - start);
            });
        }
    }

    @Override
    public void onBpmKilled(String docId, String processKey, String optSource) {
        NkDocEngineContext.startLog("BPM_KILLED", docId);
        DocHV docHV = detail(docId);
        customObjectManager.getCustomObject(docHV.getDef().getRefObjectType(), NkDocProcessor.class)
                .doOnBpmKilled(docHV, processKey, optSource);
        // 事务提交后清空缓存
        TransactionSync.runAfterCommit(()-> redisSupport.deleteHash(Constants.CACHE_DOC, docId));
        NkDocEngineContext.endLog();
    }


    private void validate(DocHV doc){
        Assert.hasText(doc.getDocId(),"单据ID不能为空");
        Assert.hasText(doc.getDocType(),"单据类型不能为空");
    }

    /**
     * 重建索引，单据没有修改，所以两个doc是一样的
     */
    @Override
    public void reDataSync(DocHV doc){
        this.dataSync(doc, doc, true);
        searchEngine.indexBeforeCommit(DocHES.from(doc));
    }

    private void execDataSync(DocHV doc, DocHV original){
        long start1 = System.currentTimeMillis();
        super.dataSync(doc, original, false);
        if(log.isInfoEnabled())log.info("{}保存单据 同步数据 耗时{}ms", NkDocEngineContext.currLog(), System.currentTimeMillis() - start1);
        long start2 = System.currentTimeMillis();
        searchEngine.indexBeforeCommit(DocHES.from(doc));
        if(log.isInfoEnabled())log.info("{}保存单据 更新索引 耗时{}ms", NkDocEngineContext.currLog(), System.currentTimeMillis() - start2);
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

        if(log.isInfoEnabled())log.info("{}验证单据业务流 docType = {} prevDocType = {}",NkDocEngineContext.currLog(), def.getDocType(), preDocType);

        DocDefFlowV flowV = def.getFlows()
                .stream()
                .filter(item -> StringUtils.equals(item.getPreDocType(), preDocType))
                .findFirst()
                .orElseThrow(()->new NkDefineException("没有找到业务流配置"));

        if(!StringUtils.equalsAny(flowV.getPreDocState(),preDocState, "@")){
            throw new NkDefineException("状态不满足条件");
        }
        if(StringUtils.isNotBlank(flowV.getRefObjectType())){
            NkDocFlowInterceptor.FlowDescribe flowDescribe = applyDocFlowInterceptor(flowV.getRefObjectType(), preDoc);
            if(!flowDescribe.isVisible()){
                throw new NkDefineException(flowDescribe.getVisibleDesc());
            }
        }
        if(log.isInfoEnabled())
            log.info("{}验证单据业务流 docType = {} 完成 耗时{}ms",
                    NkDocEngineContext.currLog(),
                    def.getDocType(),
                    System.currentTimeMillis() - now
            );
    }

    /**
     * 处理可创建的后续单据类型
     */
    private DocHV processView(DocHV docHV){

        if(StringUtils.isNotBlank(docHV.getProcessInstanceId())){
            docHV.setBpmTask(
                    bpmTaskService.taskByBusinessAndAssignee(docHV.getDocId(), SecurityUtilz.getUser().getId())
            );
            if(log.isInfoEnabled())log.info("{}获取单据任务", NkDocEngineContext.currLog());
        }

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
        if(log.isInfoEnabled())
            log.info("{}设置单据可用状态 状态 = {}",
                    NkDocEngineContext.currLog(),
                    docHV.getDef().getStatus()
            );

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

        if(StringUtils.isNotBlank(docHV.getRuntimeKey())){
            redisRuntime.set(docHV.getRuntimeKey(),docHV);
            redisRuntime.expire(docHV.getRuntimeKey(),60 * 60);//1小时
        }

        if(log.isInfoEnabled())
            log.info("{}设置单据后续操作 操作数量 = {}",
                    NkDocEngineContext.currLog(),
                    docHV.getDef().getNextFlows().size()
            );

        return docHV;
    }
}
