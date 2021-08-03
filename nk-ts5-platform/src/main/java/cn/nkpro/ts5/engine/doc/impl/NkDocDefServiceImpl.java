package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.config.mybatis.pagination.PaginationContext;
import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.engine.co.DebugContextManager;
import cn.nkpro.ts5.engine.co.NkCustomObject;
import cn.nkpro.ts5.engine.co.NKCustomObjectManager;
import cn.nkpro.ts5.engine.doc.NkCard;
import cn.nkpro.ts5.engine.doc.NkDocProcessor;
import cn.nkpro.ts5.engine.doc.interceptor.*;
import cn.nkpro.ts5.engine.doc.model.DocDefFlowV;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocDefIV;
import cn.nkpro.ts5.engine.doc.service.NkDocDefService;
import cn.nkpro.ts5.exception.TfmsException;
import cn.nkpro.ts5.orm.mb.gen.*;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/6/10.
 */
@Slf4j
@Service
public class NkDocDefServiceImpl implements NkDocDefService {

    @Autowired@SuppressWarnings("all")
    private RedisSupport<DocDefHV> redisSupport;
    @Autowired@SuppressWarnings("all")
    private RedisSupport<List<DocDefFlowV>> redisSupportFlows;
    @Autowired@SuppressWarnings("all")
    private DebugContextManager debugContextManager;
    @Autowired@SuppressWarnings("all")
    private NKCustomObjectManager customObjectManager;

    @Autowired@SuppressWarnings("all")
    private DocDefHMapper docDefHMapper;
    @Autowired@SuppressWarnings("all")
    private DocDefIMapper docDefIMapper;
    @Autowired@SuppressWarnings("all")
    private DocDefStateMapper docDefStateMapper;
    @Autowired@SuppressWarnings("all")
    private DocDefFlowMapper docDefFlowMapper;
    @Autowired@SuppressWarnings("all")
    private DocDefCycleMapper docDefCycleMapper;
    @Autowired@SuppressWarnings("all")
    private DocDefBpmMapper docDefBpmMapper;

    @Override
    public PageList<DocDefH> getPage(String docClassify,
                                     String docType,
                                     String state,
                                     String keyword,
                                     int from,
                                     int rows,
                                     String orderField,
                                     String order){

        DocDefHExample example = new DocDefHExample();

        DocDefHExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(docClassify)){
            criteria.andDocClassifyEqualTo(docClassify);
        }
        if(StringUtils.isNotBlank(docType)){
            criteria.andDocTypeEqualTo(docType);
        }
        if(StringUtils.isNotBlank(state)){
            criteria.andStateEqualTo(state);
        }else{
            criteria.andStateIn(Arrays.asList("Active","InActive"));
        }
        if(StringUtils.isNotBlank(keyword)){
            criteria.andDocNameLike(String.format("%%%s%%",keyword));
        }
        if(StringUtils.isNotBlank(orderField)){
            example.setOrderByClause(String.format("%s %s",orderField,order));
        }

        PaginationContext context = PaginationContext.init();
        List<DocDefH> list = docDefHMapper.selectByExample(example, new RowBounds(from, rows));
        return new PageList<>(list,from, rows, context.getTotal());
    }

    @Override
    public List<DocDefH> getList(String docType, int page){
        DocDefHExample example = new DocDefHExample();
        example.createCriteria()
                .andDocTypeEqualTo(docType);
        example.setOrderByClause("UPDATED_TIME DESC");
        return docDefHMapper.selectByExample(example, new RowBounds((page-1)*10, 10));
    }


    @Override
    public Map<String, Object> options(String classify){
        Predicate<Map.Entry<String,? extends NkCustomObject>> predicate = StringUtils.isBlank(classify)?null:
                (e)->StringUtils.equals(((NkDocProcessor)(e.getValue())).classify().name(),classify);
        Map<String,Object> options = new HashMap<>();
        options.put("docProcessors",            customObjectManager.getCustomObjectDescriptionList(NkDocProcessor.class,            false,predicate));
        options.put("docStateInterceptors",     customObjectManager.getCustomObjectDescriptionList(NkDocStateInterceptor.class,     true,null));
        options.put("docExecuteInterceptors",   customObjectManager.getCustomObjectDescriptionList(NkDocExecuteInterceptor.class, true,null));
        options.put("docCommittedInterceptors", customObjectManager.getCustomObjectDescriptionList(NkDocCommittedInterceptor.class, true,null));
        options.put("docCreateInterceptors",    customObjectManager.getCustomObjectDescriptionList(NkDocCreateInterceptor.class,    true,null));
        options.put("docUpdateInterceptors",    customObjectManager.getCustomObjectDescriptionList(NkDocUpdateInterceptor.class,    true,null));
        options.put("docFlowInterceptors",      customObjectManager.getCustomObjectDescriptionList(NkDocFlowInterceptor.class,      true,null));
        options.put("cards",                    customObjectManager.getCustomObjectDescriptionList(NkCard.class,                    false,null));
        return options;
    }

    @Override
    public DocDefIV getCardDescribe(String cardHandlerName){

        NkCard nkCard = customObjectManager.getCustomObject(cardHandlerName, NkCard.class);

        DocDefIV describe = new DocDefIV();
        describe.setBeanName(nkCard.getBeanName());
        describe.setCardName(nkCard.getCardName());
        describe.setDataComponentName(nkCard.getDataComponentName());
        describe.setDefComponentNames(nkCard.getAutoDefComponentNames());

        return describe;
    }

    @Override
    public DocDefHV doRun(DocDefHV docDefHV){
        Assert.isTrue(StringUtils.equals(docDefHV.getState(),"InActive"),"已激活的版本不能调试");
        validateDef(docDefHV);
        doUpdate(docDefHV, false);
        debugContextManager.addDebugResource(String.format("@%s",docDefHV.getDocType()),docDefHV);
        return docDefHV;
    }

    /**
     * 创建配置分支，增加Major
     * @param docDefHV DocDefHV
     * @return DocDefHV
     */
    @Override
    @Transactional
    public DocDefHV doBreach(DocDefHV docDefHV){

        // 从未激活的版本创建分支，从当前版本 增加Minor
        docDefHV.setVersion(UUID.randomUUID().toString());
        docDefHV.setState("InActive");

        return doUpdate(docDefHV,false);
    }

    /**
     * 保存配置
     * @param docDefHV DocDefHV
     * @param force force
     * @return DocDefHV
     */
    @Override
    @Transactional
    public DocDefHV doUpdate(DocDefHV docDefHV, boolean force){

        Assert.isTrue(Pattern.matches("^[A-Z0-9]{4}$",docDefHV.getDocType()),"单据类型必须为A-Z以及0-9组成的4位字符串");


        // 数据有效性检查
        DocDefH lastUpdatedVersion  = getLastUpdatedVersion(docDefHV.getDocType());
        NkDocProcessor docProcessor = customObjectManager.getCustomObject(docDefHV.getRefObjectType(), NkDocProcessor.class);

        if(lastUpdatedVersion!=null){
            Assert.isTrue(StringUtils.equals(lastUpdatedVersion.getDocClassify(),docProcessor.classify().name()),"对象扩展分类不一致");
        }

        if(StringUtils.isBlank(docDefHV.getVersion())){
            // 如果版本号为空，表示新创建的一个单据类型，需要检查是否已存在
            Assert.isTrue(force  || lastUpdatedVersion==null, String.format("单据类型%s已存在",docDefHV.getDocType()));
            docDefHV.setVersion(UUID.randomUUID().toString());
        }

        // status
        Assert.notEmpty(docDefHV.getStatus(),"状态不能为空");
        DocDefStateExample stateExample = new DocDefStateExample();
        stateExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        docDefStateMapper.deleteByExample(stateExample);
        docDefHV.getStatus()
                .forEach(state->{
                    Assert.hasLength(state.getDocState(),"状态 不能为空");
                    Assert.hasLength(state.getDocStateDesc(),"状态描述 不能为空");

                    state.setDocType(docDefHV.getDocType());
                    state.setVersion(docDefHV.getVersion());
                    state.setOrderBy(docDefHV.getStatus().indexOf(state));
                    state.setUpdatedTime(DateTimeUtilz.nowSeconds());

                    docDefStateMapper.insertSelective(state);
                });

        // cycles
        DocDefCycleExample cycleExample = new DocDefCycleExample();
        cycleExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        docDefCycleMapper.deleteByExample(cycleExample);
        docDefHV.getLifeCycles()
                .forEach(cycle->{
                    if(StringUtils.isNotBlank(cycle.getDocCycle())&&StringUtils.isNotBlank(cycle.getRefObjectType())){

                        customObjectManager.assertExists(cycle.getRefObjectType());

                        cycle.setDocType(docDefHV.getDocType());
                        cycle.setVersion(docDefHV.getVersion());
                        cycle.setOrderBy(docDefHV.getLifeCycles().indexOf(cycle));
                        cycle.setUpdatedTime(DateTimeUtilz.nowSeconds());
                        docDefCycleMapper.insert(cycle);
                    }
                });

        // bpm
        DocDefBpmExample bpmExample = new DocDefBpmExample();
        bpmExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        docDefBpmMapper.deleteByExample(bpmExample);
        docDefHV.getBpms()
                .forEach(bpm->{
                    if(StringUtils.isNotBlank(bpm.getProcessKey())&&StringUtils.isNotBlank(bpm.getStartBy())){

                        bpm.setDocType(docDefHV.getDocType());
                        bpm.setVersion(docDefHV.getVersion());
                        bpm.setOrderBy(docDefHV.getBpms().indexOf(bpm));
                        bpm.setUpdatedTime(DateTimeUtilz.nowSeconds());
                        docDefBpmMapper.insert(bpm);
                    }
                });


        // flow
        docDefHV.setDocEntrance(0);

        Assert.notEmpty(docDefHV.getFlows(),"业务流不能为空");
        DocDefFlowExample flowExample = new DocDefFlowExample();
        flowExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        docDefFlowMapper.deleteByExample(flowExample);
        docDefHV.getFlows()
                .forEach(flow->{
                    Assert.hasLength(flow.getPreDocType(),"业务流 前置交易 不能为空");
                    flow.setPreDocState(StringUtils.defaultIfBlank(flow.getPreDocState(),"@"));
                    flow.setDocType(docDefHV.getDocType());
                    flow.setVersion(docDefHV.getVersion());
                    flow.setState(docDefHV.getState());
                    flow.setOrderBy(docDefHV.getFlows().indexOf(flow));
                    flow.setUpdatedTime(DateTimeUtilz.nowSeconds());
                    docDefFlowMapper.insert(flow);

                    // 设置入口单据标识
                    if(StringUtils.equals(flow.getPreDocType(),"@")){
                        docDefHV.setDocEntrance(1);
                    }
                });

        // components
        DocDefIExample defIExample = new DocDefIExample();
        defIExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        docDefIMapper.deleteByExample(defIExample);
        if(docDefHV.getCards()!=null){
            docDefHV.getCards()
                    .forEach(item -> {
                        item.setDocType(docDefHV.getDocType());
                        item.setVersion(docDefHV.getVersion());
                        item.setUpdatedTime(DateTimeUtilz.nowSeconds());
                        item.setOrderBy(docDefHV.getCards().indexOf(item));
                        item.setMarkdownFlag(StringUtils.isNotBlank(item.getMarkdown())?1:0);
                        item.setCardContent(JSONObject.toJSONString(item.getConfig()));
                        docDefIMapper.insertSelective(item);
                    });
        }

        // h
        docDefHV.setState(StringUtils.defaultIfBlank(docDefHV.getState(),"InActive"));
        docDefHV.setDocClassify(docProcessor.classify().name());
        docDefHV.setMarkdownFlag(StringUtils.isBlank(docDefHV.getMarkdown())?0:1);
        docDefHV.setUpdatedTime(DateTimeUtilz.nowSeconds());
        if(docDefHMapper.selectByPrimaryKey(docDefHV)==null){
            docDefHMapper.insertSelective(docDefHV);
        }else{
            docDefHMapper.updateByPrimaryKeySelective(docDefHV);
        }

        return docDefHV;
    }

    private void clearActiveVersion(String docType){

        {
            DocDefH record = new DocDefH();
            record.setState("History");

            DocDefHExample example = new DocDefHExample();
            example.createCriteria()
                    .andDocTypeEqualTo(docType)
                    .andStateEqualTo("Active");
            docDefHMapper.updateByExampleSelective(record, example);
        }

        {
            DocDefFlow record = new DocDefFlow();
            record.setState("History");

            DocDefFlowExample example = new DocDefFlowExample();
            example.createCriteria()
                    .andDocTypeEqualTo(docType)
                    .andStateEqualTo("Active");
            docDefFlowMapper.updateByExampleSelective(record, example);
        }
    }

    /**
     * 激活配置，增加Minor
     * @param docDefHV DocDefHV
     * @return DocDefHV
     */
    @Override
    @Transactional
    public DocDefHV doActive(DocDefHV docDefHV){

        Assert.hasText(docDefHV.getValidFrom(),"请设定有效起始日期");
        Assert.hasText(docDefHV.getValidTo(),  "请设定有效起始日期");

        validateDef(docDefHV);

        // 清理已激活版本
        clearActiveVersion(docDefHV.getDocType());

        // 设置新版本
        docDefHV.setState("Active");

        docDefHV = doUpdate(docDefHV,false);
        // 一旦单据激活，则删除单据配置
        redisSupport.delete(Constants.CACHE_DEF_DOC_TYPES,docDefHV.getDocType());
        // 一旦单据激活，则删除所有的业务流缓存，避免数据不一致
        redisSupport.delete(Constants.CACHE_DEF_DOC_FLOWS);

        return docDefHV;
    }

    @Override
    @Transactional
    public void doDelete(DocDefH docDefHV, boolean force){
        if(!force){
            Assert.isTrue(!docDefHV.getState().equals("Active"),"已激活版本不能删除");
        }
        DocDefStateExample stateExample = new DocDefStateExample();
        stateExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        docDefStateMapper.deleteByExample(stateExample);

        DocDefFlowExample flowExample = new DocDefFlowExample();
        flowExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        docDefFlowMapper.deleteByExample(flowExample);

        DocDefIExample defIExample = new DocDefIExample();
        defIExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        docDefIMapper.deleteByExample(defIExample);

        docDefHMapper.deleteByPrimaryKey(docDefHV);
    }

    @Override
    public List<DocDefH> getAllDocTypes(){

        Map<String,DocDefH> cache = new HashMap<>();

        DocDefHExample example = new DocDefHExample();
        example.setOrderByClause("DOC_TYPE");

        docDefHMapper.selectByExample(example)
                .forEach(docDefH -> cache.computeIfAbsent(docDefH.getDocType(),(e)-> docDefH));

        return cache.values().stream()
                .sorted(Comparator.comparing(DocDefH::getDocType))
                .collect(Collectors.toList());
    }

    @Override
    public List<DocDefH> getEntrance(String classify){

        String today = DateTimeUtilz.todayShortString();

        DocDefHExample example = new DocDefHExample();
        DocDefHExample.Criteria criteria = example.createCriteria()
                .andValidFromLessThanOrEqualTo(today)
                .andValidToGreaterThanOrEqualTo(today)
                .andDocEntranceEqualTo(1)
                .andStateEqualTo("Active");

        if(StringUtils.isNotBlank(classify)){
            criteria.andDocClassifyEqualTo(classify);
        }

        example.setOrderByClause("DOC_TYPE, VERSION DESC");

        Map<String,DocDefH> cache = new HashMap<>();
        docDefHMapper
                .selectByExample(example)
                .forEach(docDefH -> cache.computeIfAbsent(docDefH.getDocType(),(e)-> docDefH));

        return cache.values().stream()
                .sorted(Comparator.comparing(DocDefH::getDocName))
                .collect(Collectors.toList());
    }

    private List<DocDefFlowV> getDocTypeFlows(String docType){
        // 从缓存中获取已激活的单据业务流
        Map<String, List<DocDefFlowV>> docTypeFlows = redisSupportFlows.getHashIfAbsent(Constants.CACHE_DEF_DOC_FLOWS, () -> {
            DocDefFlowExample flowExample = new DocDefFlowExample();
            flowExample.createCriteria()
                    .andStateEqualTo("Active");
            flowExample.setOrderByClause("ORDER_BY");

            Map<String, List<DocDefFlowV>> flows = new HashMap<>();

            docDefFlowMapper.selectByExample(flowExample)
                    .forEach(item ->
                            flows.computeIfAbsent(
                                    item.getDocType(),
                                    (key) -> new ArrayList<>()
                            ).add(BeanUtilz.copyFromObject(item,DocDefFlowV.class))
                    );

            return flows;
        });

        // 从调试上下文中获取正在调试的单据业务流
        List<DocDefHV> debugResources = debugContextManager.getDebugResources("@");
        debugResources.forEach(docDefHV -> docTypeFlows.put(docDefHV.getDocType(),docDefHV.getFlows()));

        // 合并业务流
        List<DocDefFlowV> flows = new ArrayList<>();
        docTypeFlows.values()
                .forEach(list->
                        flows.addAll(list.stream()
                                .filter(item->StringUtils.equalsAny(docType,item.getPreDocType()))
                                .collect(Collectors.toList()))
                );

        return flows;
    }
    /**
     * 获取运行时的单据配置
     * 根据单据类型 获取当前日期下 单据对应的配置信息
     * @param docType docType
     * @return DocDefHV
     */
    @Override
    @SuppressWarnings("unchecked")
    public DocDefHV getDocDefForRuntime(String docType){

        // 判断当前请求是否debug，如果是，先尝试从debug环境中获取配置
        DocDefHV defHV = Optional.ofNullable(
            (DocDefHV)(debugContextManager.getDebugResource(String.format("@%s", docType)))
        ).orElse(
            redisSupport.getIfAbsent(Constants.CACHE_DEF_DOC_TYPES,docType,()-> getLastActiveVersion(docType))
        );
        Assert.notNull(defHV,String.format("单据类型[%s]版本的配置没有找到或尚未激活",docType));

        deserializeDef(defHV);

        runLoopCards(defHV,false, (nkCard,item)->{
                item.setDebug(nkCard.isDebug());
                item.setConfig(nkCard.afterGetDef(defHV, item, item.getConfig()));
        });

        defHV.setNextFlows(getDocTypeFlows(docType));

        return defHV;
    }

    /**
     * 获取单据配置
     * 根据单据类型及版本号 获取单据对应的配置信息
     * @param docType docType
     * @param version version
     * @return DocDefHV
     */
    @Override
    public DocDefHV getDocDefForEdit(String docType, String version){
        DocDefHV docDefHV = debugContextManager.getDebugResource(String.format("@%s", docType));
        if(docDefHV!=null && StringUtils.equals(docDefHV.getVersion(),version)){
            return docDefHV;
        }
        return deserializeDef(fetchDocDefFromDB(docType, version));
    }

    private DocDefHV fetchDocDefFromDB(String docType, String version){

        DocDefHKey key = new DocDefHKey();
        key.setDocType(docType);
        key.setVersion(version);

        DocDefH docDefH = docDefHMapper.selectByPrimaryKey(key);
        docDefH.setMarkdown(null);
        Assert.notNull(docDefH,String.format("单据类型[%s]的配置没有找到",docType));

        DocDefHV def = BeanUtilz.copyFromObject(docDefH,DocDefHV.class);

        // status
        DocDefStateExample stateExample = new DocDefStateExample();
        stateExample.createCriteria()
                .andDocTypeEqualTo(docType)
                .andVersionEqualTo(version);
        stateExample.setOrderByClause("ORDER_BY");
        def.setStatus(docDefStateMapper.selectByExample(stateExample));

        // flows
        DocDefFlowExample flowExample = new DocDefFlowExample();
        flowExample.createCriteria()
                .andDocTypeEqualTo(docType)
                .andVersionEqualTo(version);
        flowExample.setOrderByClause("ORDER_BY");
        def.setFlows(BeanUtilz.copyFromList(docDefFlowMapper.selectByExample(flowExample),DocDefFlowV.class));

        // cycles
        DocDefCycleExample cycleExample = new DocDefCycleExample();
        cycleExample.createCriteria()
                .andDocTypeEqualTo(docType)
                .andVersionEqualTo(version);
        cycleExample.setOrderByClause("ORDER_BY");
        def.setLifeCycles(docDefCycleMapper.selectByExample(cycleExample));

        // bpm
        DocDefBpmExample bpmExample = new DocDefBpmExample();
        bpmExample.createCriteria()
                .andDocTypeEqualTo(docType)
                .andVersionEqualTo(version);
        bpmExample.setOrderByClause("ORDER_BY");
        def.setBpms(docDefBpmMapper.selectByExample(bpmExample));

        // cards
        DocDefIExample docDefIExample = new DocDefIExample();
        docDefIExample.createCriteria()
                .andDocTypeEqualTo(docType)
                .andVersionEqualTo(version);
        docDefIExample.setOrderByClause("ORDER_BY");
        def.setCards(BeanUtilz.copyFromList(docDefIMapper.selectByExampleWithBLOBs(docDefIExample),DocDefIV.class));

        return def;
    }

    private DocDefHV deserializeDef(DocDefHV docDefHV) {

        runLoopCards(docDefHV,true, (nkCard,item)->{
            item.setConfig(nkCard.deserializeDef(item));
            item.setPosition(nkCard.getPosition());
            item.setDataComponentName(nkCard.getDataComponentName());
            item.setDefComponentNames(nkCard.getAutoDefComponentNames());
            item.setCardContent(null);
        });

        return docDefHV;
    }

    /**
     * 验证配置是否可运行
     * @param docDefHV docDefHV
     */
    private void validateDef(DocDefHV docDefHV){

        customObjectManager.getCustomObject(docDefHV.getRefObjectType(), NkCustomObject.class);

        runLoopCards(docDefHV,false,(card, docDefIV)->{

        });
    }

    @Override
    public void runLoopCards(DocDefHV docDefHV, boolean ignoreError, Function function){
        for(DocDefIV docDefI : docDefHV.getCards()){
            // 找到对应的组件实现类
            NkCard nkCard = customObjectManager.getCustomObjectIfExists(docDefI.getCardKey(), NkCard.class);
            if(nkCard==null && !ignoreError){
                throw new TfmsException(String.format("自定义对象[%s]不存在",docDefI.getBeanName()));
            }
            try {
                function.run(nkCard, docDefI);
            }catch (Exception e){
                if(!ignoreError){
                    throw new TfmsException(e.getMessage(),e);
                }
            }
        }
    }
    /**
     * 获取最后一次更新
     * @param docType docType
     * @return DocDefH
     */
    private DocDefH getLastUpdatedVersion(String docType){

        DocDefHExample example = new DocDefHExample();
        example.createCriteria()
                .andDocTypeEqualTo(docType);
        example.setOrderByClause("UPDATED_TIME desc");

        return docDefHMapper.selectByExample(example, new RowBounds(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
    }
    private DocDefHV getLastActiveVersion(String docType){

        DocDefHExample example = new DocDefHExample();
        example.createCriteria()
                .andDocTypeEqualTo(docType)
                .andStateEqualTo("Active");

        return docDefHMapper
                .selectByExample(example, new RowBounds(0, 1))
                .stream()
                .findFirst()
                .map(item-> fetchDocDefFromDB(docType,item.getVersion()))
                .orElse(null);
    }
}
