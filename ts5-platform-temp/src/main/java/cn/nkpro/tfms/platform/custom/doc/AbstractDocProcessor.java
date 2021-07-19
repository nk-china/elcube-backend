package cn.nkpro.tfms.platform.custom.doc;

import cn.nkpro.tfms.platform.basis.Constants;
import cn.nkpro.tfms.platform.basis.TfmsCustomObjectManager;
import cn.nkpro.tfms.platform.basis.TfmsSpELManager;
import cn.nkpro.tfms.platform.custom.TfmsCardComponent;
import cn.nkpro.tfms.platform.custom.TfmsCardIndexItemExec;
import cn.nkpro.tfms.platform.custom.TfmsComponent;
import cn.nkpro.tfms.platform.elasticearch.ESRoot;
import cn.nkpro.tfms.platform.elasticearch.SearchEngine;
import cn.nkpro.ts5.exception.TfmsComponentException;
import cn.nkpro.ts5.exception.TfmsIllegalContentException;
import cn.nkpro.tfms.platform.mappers.gen.BizDocMapper;
import cn.nkpro.tfms.platform.mappers.gen.BizDocSysStatusMapper;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.DefDocComponentBO;
import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import cn.nkpro.tfms.platform.model.index.IndexDoc;
import cn.nkpro.tfms.platform.model.index.IndexDocItem;
import cn.nkpro.tfms.platform.model.po.BizDoc;
import cn.nkpro.tfms.platform.model.po.BizDocSysStatusExample;
import cn.nkpro.tfms.platform.model.po.BizDocSysStatusKey;
import cn.nkpro.tfms.platform.services.TfmsDocHistoryService;
import cn.nkpro.tfms.platform.services.TfmsDocService;
import cn.nkpro.tfms.platform.services.TfmsTaskService;
import cn.nkpro.tfms.platform.services.ThreadLocalContextHolder;
import cn.nkpro.ts5.supports.GUID;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import cn.nkpro.ts5.utils.LocalSyncUtilz;
import cn.nkpro.ts5.utils.SpringEmulated;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/7/13.
 */
@Slf4j
public abstract class AbstractDocProcessor implements TfmsDocProcessor{


    @Autowired
    protected GUID guid;
    @Autowired
    protected SearchEngine searchEngine;
    @Autowired
    protected RedisSupport<Object> redisSupportObject;
    @Autowired
    protected TfmsCustomObjectManager customObjectManager;

    @Autowired
    protected TfmsDocService docService;
    @Autowired
    protected TfmsTaskService taskService;
    @Autowired
    protected TfmsDocHistoryService historyService;

    @Autowired
    private BizDocMapper bizDocMapper;
    @Autowired
    private BizDocSysStatusMapper bizDocSysStatusMapper;
    @Autowired
    private TfmsSpELManager spELManager;

    @Getter
    protected String processorName;

    public AbstractDocProcessor(){
        this.processorName = parseProcessorName();
    }

    @Override
    public String getDocHeaderComponentName() {
        return "nk-page-doc-header-default";
    }

    @Override
    public Class<? extends BizDocBase> dataType() {
        return BizDocBase.class;
    }

    @Override
    public Map<String, Object> getDocProcessDef(DefDocTypeBO def) {
        if(StringUtils.isNotBlank(def.getRefObjectTypeOptions())) {
           return JSON.parseObject(def.getRefObjectTypeOptions());
        }
        return Collections.emptyMap();
    }

    @Override
    public boolean standalone() {
        return false;
    }

    /**
     * 获取单据详情
     * @param source 带配置的单据基本情况
     * @return 单据详情
     */
    @Override
    public final BizDocBase detail(BizDocBase source) {

        BizDocBase doc = BeanUtilz.copyFromObject(source, dataType());

        // 获取单据的交易伙伴名称
        if(!StringUtils.equals(doc.getDocId(),doc.getPartnerId()) && StringUtils.isNotBlank(doc.getPartnerId())){
            BizDocBase partner = docService.getDocDetail(doc.getPartnerId());
            doc.setPartnerName(partner.getPartnerName());
        }

        @SuppressWarnings("all")
        Set<String> sysStatus = (Set<String>) redisSupportObject.getIfAbsent(
                String.format("%s%s", Constants.CACHE_DOC , doc.getDocId()),
                "$sysStatus",
                ()->{
                    // 获取系统状态
                    BizDocSysStatusExample exampleSysStatus = new BizDocSysStatusExample();
                    exampleSysStatus.createCriteria().andDocIdEqualTo(doc.getDocId());
                    return bizDocSysStatusMapper.selectByExample(exampleSysStatus)
                            .stream()
                            .map(BizDocSysStatusKey::getSysState)
                            .collect(Collectors.toSet());
                }
        );
        doc.setSysStatus(sysStatus);

        log.debug(">>>准备执行组件的获取数据方法");
        doInCardComponents(doc.getDocId(), doc.getDefinedDoc(), false, (component,defDocComponent) ->{

            Object obj;
            if(component.cached()){
                obj = redisSupportObject.getIfAbsent(
                        String.format("%s%s", Constants.CACHE_DOC , doc.getDocId()),
                        component.getComponentName(),
                        true,
                        ()->{
                            try {
                                // 执行组件getData方法
                                return component.getData(doc);
                            } catch (Exception e) {
                                throw new TfmsComponentException(component.getComponentName(), e);
                            }
                        }
                );
            }else{
                try {
                    obj  = component.getData(doc);
                } catch (Exception e) {
                    throw new TfmsComponentException(component.getComponentName(), e);
                }
            }
            doc.getComponentsData().put(defDocComponent.getComponent(),obj);

            try {
                component.afterGetData(doc, doc.getDefinedDoc());
            }catch (Exception e){
                throw new TfmsComponentException(component.getComponentName(), e);
            }
        });
        log.debug("<<<执行组件的获取数据方法完成");

        doInCardComponents(doc.getDocId(), doc.getDefinedDoc(), false, (component,defDocComponent) ->{
            try {
                component.processData(doc);
            } catch (Exception e) {
                throw new TfmsComponentException(component.getComponentName(), e);
            }
        });
        return detailAfterComponent(doc);
    }
    /**
     * 在调用组件方法后执行
     * @param doc doc
     * @return doc
     */
    public BizDocBase detailAfterComponent(BizDocBase doc) {
        return doc;
    }

    /**
     * 预创建单据
     * @param def 单据配置
     * @param refObjectId 引用的对象ID
     * @param preDocId 前序单据ID
     * @return 预创建的单据
     */
    @Override
    public final BizDocBase create(DefDocTypeBO def, String refObjectId, String preDocId) {

        //preDoc 可能为null
        BizDocBase preDoc    = StringUtils.isNotBlank(preDocId)? docService.getDocDetail(preDocId):null;

        log.debug("初始化单据数据");
        BizDocBase doc = newInstance();
        if(doc==null){
            throw new TfmsIllegalContentException("创建单据发生错误");
        }

        // 核心字段
        doc.setDocId(guid.nextId(BizDoc.class));
        doc.setClassify(def.getDocClassify());
        doc.setDocType(def.getDocType());
        doc.setDocName(def.getDocName());
        doc.setDocState(def.getStatus().get(0).getDocState());

        // 关联字段
        doc.setRefObjectId(refObjectId);
        doc.setPreDocId(StringUtils.defaultIfBlank(preDocId,Constants.BIZ_DEFAULT_EMPTY));

        // 系统字段
        doc.setCreatedTime(DateTimeUtilz.nowSeconds());
        doc.setUpdatedTime(DateTimeUtilz.nowSeconds());
        doc.setDefVersion(def.getVersion());

        // 引用数据
        doc.setPreDoc(preDoc);
        doc.setDefinedDoc(def);

        BizDocBase finalDoc = createBeforeComponent(doc);

        log.debug(">>>准备执行组件的创建方法");
        doInCardComponents(doc.getDocId(), def, true, (TfmsCardComponent component, DefDocComponentBO defDocComponent) ->{

            log.debug("   "+component);
            // 执行组件doCreate方法
            Object object;
            try {
                object = component.create(finalDoc,preDoc,def);
            } catch (Exception e) {
                throw new TfmsComponentException(component.getComponentName(), e);
            }
            // 组件不要给默认值，不然容易导致组件的数据格式混乱
            // object = object == null? Collections.EMPTY_MAP:object;
            finalDoc.getComponentsData().put(defDocComponent.getComponent(),object);

            try {
                component.afterGetData(doc, doc.getDefinedDoc());
            }catch (Exception e){
                throw new TfmsComponentException(component.getComponentName(), e);
            }
        });

//        doInCardComponents(doc.getDocId(), doc.getDefinedDoc(),false,  (component,defDocComponent) ->{
//            try {
//                component.processData(doc);
//            } catch (Exception e) {
//                throw new TfmsComponentException(component.getComponentName(), e);
//            }
//        });
        log.debug("<<<执行组件的更新方法完成");

        log.debug("===> 单据已生成");

        return createAfterComponent(finalDoc);
    }

    /**
     * 在调用组件方法前执行
     * @param doc docc
     * @return doc
     */
    public BizDocBase createBeforeComponent(BizDocBase doc) {
        return doc;
    }

    /**
     * 在调用组件方法后执行
     * @param doc doc
     * @return doc
     */
    public BizDocBase createAfterComponent(BizDocBase doc) {
        return doc;
    }


    /**
     * 单据运算
     * @param calculate calculate
     * @param source source
     * @return doc
     */
    @Override
    public final BizDocBase calculate(String component, String calculate, BizDocBase source) {

        deserializeDoc(source);
        BizDocBase doc = calculateBeforeComponent(calculate, source);
        /*
         * 循环执行组件的doUpdate方法
         */
        log.debug(">>>准备执行组件的计算方法");

        doInCardComponents(doc.getDocId(), doc.getDefinedDoc(), true, (c,defDocComponent) ->{
            log.debug("   "+c.getComponentName());
            // 执行组件calculate方法
            try {
                if(StringUtils.isBlank(component)||StringUtils.equals(component,c.getComponentName())){
                    c.calculate(
                            calculate,
                            doc,
                            doc.getDefinedDoc()
                    );
                }
            } catch (Exception e) {
                throw new TfmsComponentException(c.getComponentName(), e);
            }
        });
        log.debug("<<<执行组件的更新方法完成");
        return calculateAfterComponent(calculate, doc);
    }


    /**
     * 单据运算
     * @param event event
     * @param doc doc
     * @return obj
     */
    @Override
    public final Object call(String component, String event, BizDocBase doc) {
        deserializeDoc(doc);
        TfmsCardComponent tfmsCard = customObjectManager.getCustomObject(component, TfmsCardComponent.class);
        try {
            return tfmsCard.call(event,doc,doc.getDefinedDoc());
        } catch (Exception e) {
            throw new TfmsComponentException(component, e);
        }
    }

    /**
     * 在调用组件方法前执行
     * @param calculate calculate
     * @param doc doc
     * @return
     */
    @SuppressWarnings("all")
    public BizDocBase calculateBeforeComponent(String calculate, BizDocBase doc){
        return doc;
    }

    /**
     * 在调用组件方法后执行
     * @param calculate calculate
     * @param doc doc
     * @return
     */
    @SuppressWarnings("all")
    public BizDocBase calculateAfterComponent(String calculate, BizDocBase doc){
        return doc;
    }

    @Override
    public final BizDocBase update(BizDocBase source, BizDocBase original, String optSource) {
        /*
         * 获取单据数据
         */
        log.debug("获取单据的原始数据");

        deserializeDoc(source);
        log.info("original更新时是否为空------------------"+JSON.toJSON(original));
        BizDocBase doc = updateBeforeComponent(source,original==null);
        /*
         * 处理系统状态
         */
        BizDocSysStatusExample exampleSysStatus = new BizDocSysStatusExample();
        exampleSysStatus.createCriteria().andDocIdEqualTo(doc.getDocId());
        bizDocSysStatusMapper.deleteByExample(exampleSysStatus);
        doc.setSysStatus(Collections.emptySet());

        doc.getDefinedDoc()
                .getStatus()
                .stream()
                .filter(defDocStatus -> StringUtils.equals(defDocStatus.getDocState(),doc.getDocState()))
                .findAny()
                .ifPresent(defDocStatus -> {

                    doc.setSysStatus(Arrays.stream(StringUtils.split(StringUtils.defaultString(defDocStatus.getSysState()),'|'))
                            .collect(Collectors.toSet()));
                    doc.getSysStatus().forEach(sysState->{
                        BizDocSysStatusKey status = new BizDocSysStatusKey();
                        status.setDocId(doc.getDocId());
                        status.setSysState(sysState);
                        bizDocSysStatusMapper.insert(status);

                    });
                });

        /*
         * 循环执行组件的doUpdate方法
         */
        log.debug(">>>准备执行组件的更新方法");

        List<String> changedCard = new ArrayList<>();

        doInComponents(doc.getDocId(), doc.getDefinedDoc(),false,  (component,defDocComponent) ->{
            log.debug("   "+component.getComponentName());
            // 执行组件doUpdate方法

            if(original!=null){

                Object o1 = doc.getComponentsData().get(component.getComponentName());
                Object o2 = original.getComponentsData().get(component.getComponentName());

                // 记录前端修改过的card
                if(!Objects.equals(JSONObject.toJSONString(o1),JSONObject.toJSONString(o2))){
                    changedCard.add(component.getComponentName());
                }
            }else{
                changedCard.add(component.getComponentName());
            }

            try {
                component.update(
                        doc,
                        doc.getDefinedDoc(),
                        original
                );
            } catch (Exception e) {
                throw new TfmsComponentException(component.getComponentName(), e);
            }
        });
        log.debug("<<<执行组件的更新方法完成");

        // 工作流
        if(doc.getDefinedDoc().getBpm()!=null){ // 工作流配置不为空
            if(StringUtils.equals(doc.getDefinedDoc().getBpm().getStartBy(),doc.getDocState())){ // 满足启动条件
                if(original==null || !StringUtils.equals(original.getDocState(),doc.getDocState())){ // 满足状态条件

                    ThreadLocalContextHolder.setRuntimeDoc(doc);
                    taskService.start(doc.getDefinedDoc().getBpm().getProcessKey(),doc.getDocId());
                    ThreadLocalContextHolder.clearRuntimeDoc();
                }
            }
        }

        final BizDocBase finalDoc = updateAfterComponent(doc,original==null);

        /*
         * 记录日志
         */
        historyService.doAddVersion(finalDoc,original,changedCard,optSource);

        LocalSyncUtilz.runAfterCommit(()-> updateCommited(finalDoc,original==null));

        return finalDoc;
    }
    public BizDocBase updateBeforeComponent(BizDocBase doc, boolean isCreate) {
        return doc;
    }

    public BizDocBase updateAfterComponent(BizDocBase doc, boolean isCreate) {
        return doc;
    }

    @Override
    public void updateBeforeCommit(BizDocBase doc) {}

    @SuppressWarnings("all")
    public void updateCommited(BizDocBase doc, boolean isCreate) {}

    @Override
    public void stateChanged(BizDocBase doc, String oldDocState) {}

    /**
     * 将单据数据提交给搜索引擎
     * @param docBO doc
     */
    @Override
    public final void index(BizDocBase docBO){

        List<ESRoot> indexDocs = new ArrayList<>();
        List<String> itemTypes = new ArrayList<>();

        IndexDoc indexDoc = buildIndex(docBO);
        indexDocs.add(indexDoc);

        doInCardComponents(docBO.getDocId(), docBO.getDefinedDoc(),false,  (component,defDocComponent) ->{
            try {
                TfmsCardIndexItemExec itemExec = component.indexItem(docBO, docBO.getDefinedDoc(), indexDoc);
                if(itemExec!=null){
                    if(!CollectionUtils.isEmpty(itemExec.getUpdate())){
                        indexDocs.addAll(itemExec.getUpdate());
                    }
                    if(StringUtils.isNotBlank(itemExec.getRemoveItemType())){
                        itemTypes.add(itemExec.getRemoveItemType());
                    }
                }
            } catch (Exception e) {
                throw new TfmsComponentException(component.getComponentName(), e);
            }
        });

        itemTypes.forEach(itemType-> searchEngine.deleteAfterCommit(IndexDocItem.class,
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("docId",docBO.getDocId()))
                        .must(QueryBuilders.termQuery("itemType",itemType))
        ));
        searchEngine.indexAfterCommit(indexDocs);
    }

    @Override
    public final IndexDoc buildIndex(BizDocBase docBO){
        IndexDoc docIndex = BeanUtilz.copyFromObject(docBO, IndexDoc.class);

        docIndex.setTags(StringUtils.split(docBO.getDocTags(),','));
        docIndex.setDocTypeDesc(String.format("%s | %s",docBO.getDocType(),docBO.getDefinedDoc().getDocName()));
        docBO.getDefinedDoc().getStatus().stream()
                .filter(defDocStatus -> StringUtils.equals(defDocStatus.getDocState(),docBO.getDocState()))
                .findAny()
                .ifPresent(defDocStatus -> docIndex.setDocStateDesc(
                        String.format("%s | %s",defDocStatus.getDocState(),defDocStatus.getDocStateDesc())
                ));

        EvaluationContext context = spELManager.createContext(docBO);
        docBO.getDefinedDoc().getIndexRules()
                .forEach(indexRule -> {
                    Expression exp = spELManager.parser().parseExpression(indexRule.getRule());
                    setEsDocFiled(docIndex,indexRule.getField(),exp.getValue(context));
                });

        return buildIndex(docBO,docIndex);
    }

    public IndexDoc buildIndex(BizDocBase docBO, IndexDoc docIndex){
        return docIndex;
    }

    @Override
    public Object exec(String json) {
        // 在这里可以执行一些运算
        return null;
    }


    private void doInCardComponents(String docId, DefDocTypeBO defDocTypeBO, boolean loop, RunInCardComponents runInCardComponents) {
        doInComponents(docId,defDocTypeBO,loop,(component, defDocComponent)->{
            if(component instanceof TfmsCardComponent){
                runInCardComponents.run((TfmsCardComponent) component,defDocComponent);
            }
        });
    }
    private void doInComponents(String docId, DefDocTypeBO defDocTypeBO, boolean loop, RunInComponents runInComponents) {
        /*
         * 为了避免在组件中访问单据方法，引起循环调用，在执行组件方法前，进行单据锁定
         */
        ThreadLocalContextHolder.lockBizDoc(docId);
        try{
            List<DefDocComponentBO> collect = defDocTypeBO.getCustomComponents()
                    .stream()
                    .sorted(Comparator.comparingInt(c -> (c.getOrderCalc() == null ? -1 : c.getOrderCalc())))
                    .collect(Collectors.toList());
            int times = 1;
            do{
                boolean retry = false;

                log.info("第"+times+"次计算");

                for(DefDocComponentBO defDocComponent : collect){

                    int timesCalc = defDocComponent.getTimesCalc()!=null ? defDocComponent.getTimesCalc() : 1;
                    if(timesCalc>=times){
                        // 找到对应的组件实现类
                        runInComponents.run(
                                customObjectManager.getCustomObject(defDocComponent.getComponent(), TfmsComponent.class),
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
        void run(TfmsComponent component, DefDocComponentBO defDocComponent);
    }

    @FunctionalInterface
    private interface RunInCardComponents{
        void run(TfmsCardComponent component, DefDocComponentBO defDocComponent);
    }

    private void setEsDocFiled(IndexDoc esDoc, String field, Object value){

        Optional<Field> optional = Arrays.stream(esDoc.getClass().getDeclaredFields())
                .filter(f -> f.getName().equals(field))
                .findFirst();
        if(optional.isPresent()){
            Field f = optional.get();
            f.setAccessible(true);
            try {
                f.set(esDoc,value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }else{
            esDoc.addDynamicField(field,value);
        }
    }

    /**
     * 创建一个单据数据对象
     * @return doc
     */
    private BizDocBase newInstance(){
        try {
            return dataType().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    /**
     * 对前端提交的单据数据进行反序列化
     * @param doc doc
     */
    private void deserializeDoc(BizDocBase doc){
        log.debug("对单据数据进行反序列化");
        doInCardComponents(doc.getDocId(), doc.getDefinedDoc(), false, (component,defDocComponent) ->{
            Object componentData = doc.getComponentsData().get(component.getComponentName());
            Object componentDefData = doc.getDefinedDoc().getCustomComponentsDef().get(component.getComponentName());
            try {
                doc.getComponentsData().put(component.getComponentName(),component.deserialize(componentData));
                doc.getDefinedDoc().getCustomComponentsDef().put(component.getComponentName(),component.deserializeDef(componentDefData));
            } catch (Exception e) {
                throw new TfmsComponentException(component.getComponentName(), e);
            }
        });
    }

    private String parseProcessorName(){

        Class<? extends AbstractDocProcessor> clazz = getClass();

        Component component = clazz.getDeclaredAnnotation(Component.class);
        if(component!=null && StringUtils.isNotBlank(component.value()))
            return component.value();

        Service service = clazz.getDeclaredAnnotation(Service.class);
        if(service!=null && StringUtils.isNotBlank(service.value()))
            return service.value();

        return SpringEmulated.decapitalize(clazz.getSimpleName());
    }
}
