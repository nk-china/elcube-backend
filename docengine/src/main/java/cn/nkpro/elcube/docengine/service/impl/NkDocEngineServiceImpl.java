/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.docengine.service.impl;

import cn.nkpro.elcube.basic.Constants;
import cn.nkpro.elcube.basic.PageList;
import cn.nkpro.elcube.basic.TransactionSync;
import cn.nkpro.elcube.data.mybatis.pagination.PaginationContext;
import cn.nkpro.elcube.docengine.NkDocEngine;
import cn.nkpro.elcube.docengine.NkDocEngineThreadLocal;
import cn.nkpro.elcube.docengine.NkDocProcessor;
import cn.nkpro.elcube.docengine.gen.DocDefI;
import cn.nkpro.elcube.docengine.gen.DocH;
import cn.nkpro.elcube.docengine.gen.DocHExample;
import cn.nkpro.elcube.docengine.model.DocDefHV;
import cn.nkpro.elcube.docengine.model.DocHPersistent;
import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.docengine.model.DocState;
import cn.nkpro.elcube.docengine.model.es.DocHES;
import cn.nkpro.elcube.docengine.service.NkDocEngineFrontService;
import cn.nkpro.elcube.docengine.service.NkDocPermService;
import cn.nkpro.elcube.utils.BeanUtilz;
import cn.nkpro.elcube.utils.UUIDHexGenerator;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Comparator;
import java.util.Optional;

@Slf4j
@Service
public class NkDocEngineServiceImpl extends AbstractNkDocEngine implements NkDocEngineFrontService {

    @Autowired
    private NkDocFinder docFinder;

    /**
     * 从数据库表中分页查询单据列表
     */
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
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
    @Transactional(propagation = Propagation.REQUIRED)
    public DocHV create(String docType, String preDocId, String optSource, Function function) {

        DocHV docHV = createDocHV(docType, preDocId);

        function.apply(docHV);

        return execUpdate(docHV, optSource);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public DocHV createForView(String docType, String preDocId) {

        // 启用本地线程变量
        NkDocEngineThreadLocal.enableLocalDoc();

        if(log.isInfoEnabled())log.info("创建单据 开始");

        docPermService.assertHasDocPerm(NkDocPermService.MODE_WRITE, docType);

        DocHV docHV = createDocHV(docType, preDocId);

        docHV.setRuntimeKey("runtime:"+ docHV.getDocId()+":"+UUIDHexGenerator.generate());

        return docToView(docHV);
    }

    private DocHV createDocHV(String docType, String preDocId){
        // 获取前序单据
        //DocHV preDoc = StringUtils.isBlank(preDocId) || StringUtils.equalsIgnoreCase(preDocId,"@") ? null : detail(preDocId);
        DocHV preDoc = null;
        if(StringUtils.isNotBlank(preDocId) && !StringUtils.equalsIgnoreCase(preDocId,"@")){
            if(NkDocEngineThreadLocal.existUpdated(preDocId)){
                preDoc = NkDocEngineThreadLocal.getUpdated(preDocId);
            }else{
                preDoc = detail(preDocId);
            }
        }
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
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public DocState state(String docId) {

        DocHPersistent docHPersistent = fetchDoc(docId);

        return BeanUtilz.copyFromObject(docHPersistent, DocState.class);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public DocHV detailView(String docId, boolean edit) {

        // 启用本地线程变量
        NkDocEngineThreadLocal.enableLocalDoc();

        final long start = System.currentTimeMillis();

        try{

            if(log.isInfoEnabled()){
                log.info("获取单据视图");
            }

            // 先获取单据的持久化数据，主要为了获取单据类型，来判断权限
            DocHPersistent docHPersistent = fetchDoc(docId);

            // 检查READ权限
            if(!debugContextManager.isDebug()) {
                docPermService.assertHasDocPerm(NkDocPermService.MODE_READ, docId, docHPersistent.getDocType());
            }

            // 尝试先从本地线程中获取单据对象
            // 因为本地单据clone后会导致数据反序列化找不到脚本编译的Class，所以暂时放弃这个方案
            //DocHV docHV = NkDocEngineContext.getDoc(
            //        docId,
            //        (id)-> persistentToHV(docHPersistent)
            //);
            DocHV docHV = persistentToHV(docHPersistent);

            Assert.notNull(docHV,"单据不存在");

            // redisRuntime.keys("runtime:"+docId);

            return docToView(docHV);

        }finally {

            if(log.isInfoEnabled()){
                log.info("获取单据视图 完成 总耗时{}ms",System.currentTimeMillis() - start);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public DocHV detail(String docId) {

        final long start = System.currentTimeMillis();
        try{
            if(log.isInfoEnabled())log.info("获取单据");

            // 优先从本地线程变量中获取单据
            // 本地线程变量需要显示调用NkDocEngineThreadLocal.enableLocalDoc()来启用
            return NkDocEngineThreadLocal.localDoc(docId, (id)->persistentToHV(fetchDoc(id)));
        }finally {
            if(log.isInfoEnabled())log.info("获取单据 完成 总耗时{}ms",System.currentTimeMillis() - start);
        }
    }

    @Override
    public DocHV detail(String docType, String businessKey) {

        DocHExample example = new DocHExample();
        example.createCriteria()
                .andDocTypeEqualTo(docType)
                .andBusinessKeyEqualTo(businessKey);

        return docHMapper.selectByExample(example, new RowBounds(0, 1))
                .stream()
                .findFirst()
                .map(DocH::getDocId)
                .map(this::detail)
                .orElse(null);
    }

    @Override
    public NkDocFinder find(String... docType) {
        return docFinder.createFinder(docType);
    }

    /**
     *
     * @throws IllegalTransactionStateException 在只读transaction状态下执行；
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public DocHV calculate(DocHV doc, String fromCard, Object options) {
        if(log.isInfoEnabled())log.info("开始单据计算");
        try{

            validate(doc);
            DocDefHV defHV = docDefService.deserializeDef(doc.getDef());

            runtimeAppend(doc);

            // 获取单据处理器 并执行
            doc =  customObjectManager
                    .getCustomObject(defHV.getRefObjectType(), NkDocProcessor.class)
                    .calculate(doc, fromCard, options);

            doc.setDef(defHV);

            return docToView(doc);

        }finally {
            if(log.isInfoEnabled())log.info("单据计算 完成");
        }
    }

    /**
     *
     * @throws IllegalTransactionStateException 在无transaction状态下执行；如果当前已有transaction，则抛出异常
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Object call(DocHV doc, String fromCard, String method, Object options) {
        if(log.isInfoEnabled())log.info("开始调用单据程序");
        try{
            validate(doc);

            // 获取单据配置
            DocDefHV def = docDefService.deserializeDef(doc.getDef());

            // 获取单据处理器 并执行
            return customObjectManager
                    .getCustomObject(def.getRefObjectType(), NkDocProcessor.class)
                    .call(doc, fromCard, method, options);
        }finally {
            if(log.isInfoEnabled())log.info("调用单据程序 完成");
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


        if (log.isInfoEnabled())
            log.info("开始保存单据视图");

        docPermService.assertHasDocPerm(NkDocPermService.MODE_WRITE, docHV.getDocType());


        if (log.isInfoEnabled()){
            log.info(StringUtils.EMPTY);
            log.info("1.第一次获取原始单据 用于填充被权限过滤的数据");
        }
        DocHV runtime = runtimeAppend(docHV);


        if (log.isInfoEnabled()){
            log.info(StringUtils.EMPTY);
            log.info("2.反序列化配置");
        }
        //DocDefHV def = docDefService.deserializeDef(docHV.getDef());

        docHV.setDef(runtime.getDef());



        // 如果单据为修改模式下，检查是否有该单据的write权限
        if (!runtime.isNewCreate()) {
            if (!debugContextManager.isDebug()) {
                docPermService.assertHasDocPerm(NkDocPermService.MODE_WRITE, docHV.getDocId(), docHV.getDocType());
            }
        }


        if (log.isInfoEnabled()){
            log.info(StringUtils.EMPTY);
            log.info("3.执行更新逻辑");
        }
        docHV = execUpdate(docHV, optSource);
        //docHV.setDef(def);


        if (log.isInfoEnabled()){
            log.info(StringUtils.EMPTY);
            log.info("4.将单据数据做界面显示处理");
        }
        docToView(docHV);

        if (debugContextManager.isDebug()) {
            debugContextManager.addDebugResource("$" + docHV.getDocId(), docHV.toPersistent());
        }

        if (log.isInfoEnabled()){
            log.info(StringUtils.EMPTY);
            log.info("5.更新后清理过程引用");
        }
        runtimeClear(docHV);
        docHV.clearItemContent();
        return docHV;
    }

    @Override
    @Transactional
    public DocHV doUpdate(String docId, String optSource, NkDocEngine.Function function){

        DocHV doc = detail(docId);
        function.apply(doc);
        doc = execUpdate(doc, optSource);
        doc.clearItemContent();

        return doc;
    }

    @Override
    @Transactional
    public DocHV doUpdateAgain(String docId, String optSource, NkDocEngine.Function function){

        DocHV doc = NkDocEngineThreadLocal.getUpdated(docId);
        function.apply(doc);
        doc = execUpdate(doc, optSource);
        doc.clearItemContent();

        return doc;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DocHV random(DocHV doc) {
        if(log.isInfoEnabled())log.info("开始生成随机数据");
        try{

            validate(doc);
            DocDefHV def = docDefService.deserializeDef(doc.getDef());

            // 获取单据处理器 并执行
            return customObjectManager
                    .getCustomObject(def.getRefObjectType(), NkDocProcessor.class)
                    .random(doc);
        }finally {
            if(log.isInfoEnabled())log.info("生成随机数据 完成");
        }
    }


    @Transactional
    @Override
    public void onBpmKilled(String docId, String processKey, String optSource) {
        DocHV docHV = detail(docId);
        customObjectManager.getCustomObject(docHV.getDef().getRefObjectType(), NkDocProcessor.class)
                .doOnBpmKilled(docHV, processKey, optSource);
        // 事务提交后清空缓存
        TransactionSync.runAfterCommit("清除单据缓存"+docId, ()-> redisSupport.deleteHash(Constants.CACHE_DOC, docId));
    }


    /**
     * 重建索引，单据没有修改，所以两个doc是一样的
     */
    @Transactional
    @Override
    public void reDataSync(DocHV doc){
        this.dataSync(doc, doc, true);
        searchEngine.indexBeforeCommit(DocHES.from(doc));
    }


    private DocHV execUpdate(DocHV doc, String optSource){
        final long      start = System.currentTimeMillis();
        String          docId = doc.getDocId();

        if(log.isInfoEnabled())
            log.info("开始保存单据");

        validate(doc);


        // 获取原始单据数据
        if(log.isInfoEnabled()){
            log.info(StringUtils.EMPTY);
            log.info("准备获取原始单据 用于对比单据修改前后的差异");
        }
        Optional<DocHV> optionalOriginal = Optional.ofNullable(detail(doc.getDocId()));
        if(log.isInfoEnabled()){
            log.info("完成获取原始单据");
            log.info(StringUtils.EMPTY);
        }


        if(optionalOriginal.isPresent()){
            // 修改
            // 检查单据数据是否已经发生变化
            Assert.isTrue(StringUtils.equals(doc.getIdentification(),optionalOriginal.get().getIdentification()),
                    "单据被其他用户修改，请刷新后重试");
        }else{
            // 新建
            // 检查单据是否符合业务流控制
            // 获取单据配置
            DocDefHV def = docDefService.deserializeDef(doc.getDef());
            validateFlow(def, detail(doc.getPreDocId()));
        }

        // 获取单据处理器 并执行
        doc = customObjectManager
                .getCustomObject(doc.getDef().getRefObjectType(), NkDocProcessor.class)
                .doUpdate(doc, optionalOriginal.orElse(null),optSource);

        if(log.isInfoEnabled())
            log.info("保存单据内容 创建重建index任务");

        execDataSync(doc, optionalOriginal.orElse(null));

        // 预创建一个持久化对象，在事务提交后使用
        DocHPersistent docHPersistent = doc.toPersistent();

        // tips: 先删除缓存，避免事务提交成功后，缓存更新失败
        redisSupport.deleteHash(Constants.CACHE_DOC, docId);

        TransactionSync.runAfterCompletion("更新单据缓存"+docId,(status)-> {

            if(status == TransactionSynchronization.STATUS_COMMITTED){
                // 如果事务更新成功，将更新后的单据更新到缓存
                redisSupport.set(Constants.CACHE_DOC, docId, docHPersistent);
                if(log.isInfoEnabled())log.info("更新缓存");
            }

            if(log.isInfoEnabled())log.info("保存单据 完成 总耗时{}ms", System.currentTimeMillis() - start);
        });

        return doc;
    }

    /**
     *
     * 因为前端回传的单据配置是经过权限过滤的，不完整的，
     * 所以需要重新获取完整的单据和数据进行处理
     * 涉及方法：
     * create(String, String, String, NkDocEngine.Function)
     * calculate(DocHV, String, Object)
     * doUpdateView(DocHV, String)
     *
     * 逻辑处理完成后，再进行权限过滤，返回给前端
     * @see #docToView(DocHV)
     *
     */
    private DocHV runtimeAppend(DocHV doc){

        // 获取单据运行时状态，如果runtimeKey为空，获取当前最新单据作为运行时
        DocHV docHVRuntime;
        if(StringUtils.isNotBlank(doc.getRuntimeKey())){
            docHVRuntime = redisRuntime.get(doc.getRuntimeKey());

            Assert.notNull(docHVRuntime,"操作已超时");
        }else{
            docHVRuntime = detail(doc.getDocId());
            Assert.notNull(docHVRuntime,"原始单据不存在");

            doc.setRuntimeKey("runtime:"+ doc.getDocId()+":"+ UUIDHexGenerator.generate());
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
}
