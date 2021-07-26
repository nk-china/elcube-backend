package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.basic.NKCustomObject;
import cn.nkpro.ts5.basic.NKCustomObjectManager;
import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.config.mybatis.pagination.PaginationContext;
import cn.nkpro.ts5.engine.doc.NKCard;
import cn.nkpro.ts5.engine.doc.NKDocProcessor;
import cn.nkpro.ts5.engine.doc.NKDocStateInterceptor;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocDefIV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.doc.service.NKDocDefService;
import cn.nkpro.ts5.model.mb.gen.*;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import cn.nkpro.ts5.utils.VersioningUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class NKDocDefServiceImpl implements NKDocDefService {

    @Autowired
    private RedisSupport<DocDefHV> redisSupport;
    @Autowired
    private NKCustomObjectManager customObjectManager;

    @Autowired
    private DocDefHMapper docDefHMapper;
    @Autowired
    private DocDefIMapper docDefIMapper;
    @Autowired
    private DocDefStateMapper docDefStateMapper;
    @Autowired
    private DocDefFlowMapper docDefFlowMapper;

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
        if(StringUtils.isNotBlank(docClassify))
            criteria.andDocClassifyEqualTo(docClassify);
        if(StringUtils.isNotBlank(docType))
            criteria.andDocTypeEqualTo(docType);
        if(StringUtils.isNotBlank(state))
            criteria.andStateEqualTo(state);
        if(StringUtils.isNotBlank(keyword))
            criteria.andDocNameLike(String.format("%%%s%%",keyword));
        if(StringUtils.isNotBlank(orderField)){
            example.setOrderByClause(String.format("%s %s",orderField,order));
        }

        PaginationContext context = PaginationContext.init();
        List<DocDefH> list = docDefHMapper.selectByExample(example, new RowBounds(from, rows));
        return new PageList<>(list,from, rows, context.getTotal());
    }


    @Override
    public Map<String, Object> options(String classify){
        Predicate<Map.Entry<String,? extends NKCustomObject>> predicate = StringUtils.isBlank(classify)?null:
                (e)->StringUtils.equals(((NKDocProcessor)(e.getValue())).classify().name(),classify);
        Map<String,Object> options = new HashMap<>();
        options.put("docProcessors",        customObjectManager.getCustomObjectDescriptionList(NKDocProcessor.class,false,predicate));
        options.put("docStateInterceptors", customObjectManager.getCustomObjectDescriptionList(NKDocStateInterceptor.class,true,null));
        options.put("cards",                customObjectManager.getCustomObjectDescriptionList(NKCard.class,false,null));
        return options;
    }

    @Override
    public DocDefIV getCardDescribe(String cardHandlerName){
        return BeanUtilz.copyFromObject(customObjectManager.getCustomObject(cardHandlerName,NKCard.class), DocDefIV.class);
    }

    /**
     * 编辑前操作，如果当前版本已激活，那么增加Patch
     * @param docDefHV
     * @return
     */
    @Override
    public DocDefHV doEdit(DocDefHV docDefHV){
        if(StringUtils.equals(docDefHV.getState(),"Active")){
            DocDefH lastUpdatedVersion = getLastUpdatedVersion(docDefHV.getDocType(), VersioningUtils.parseMinor(docDefHV.getVersion()));
            // 增加Patch
            docDefHV.setVersion(VersioningUtils.nextPatch(lastUpdatedVersion.getVersion()));
            docDefHV.setValidFrom(null);
            docDefHV.setValidTo(null);
            docDefHV.setState("InActive");
            return doUpdate(docDefHV,false);
        }
        return docDefHV;
    }

    /**
     * 创建配置分支，增加Major
     * @param docDefHV
     * @return
     */
    @Override
    public DocDefHV doBreach(DocDefHV docDefHV){

        if(docDefHV.getState().equals("Active")){
            // 从已激活的版本创建分支，从当前最大版本 增加Major
            DocDefH lastVersionDef = getLastUpdatedVersion(docDefHV.getDocType(),null);
            docDefHV.setVersion(VersioningUtils.nextMajor(lastVersionDef.getVersion()));
        }else{
            // 从未激活的版本创建分支，从当前版本 增加Minor
            DocDefH lastVersionDef = getLastUpdatedVersion(docDefHV.getDocType(),VersioningUtils.parseMajor(docDefHV.getVersion()));
            docDefHV.setVersion(VersioningUtils.nextMinor(lastVersionDef.getVersion()));
        }
        docDefHV.setValidFrom(null);
        docDefHV.setValidTo(null);
        docDefHV.setState("InActive");

        return doUpdate(docDefHV,false);
    }

    /**
     * 激活配置，增加Minor
     * @param docDefHV
     * @return
     */
    @Override
    public DocDefHV doActive(DocDefHV docDefHV){

        Assert.hasText(docDefHV.getValidFrom(),"请设定有效起始日期");
        Assert.hasText(docDefHV.getValidTo(),  "请设定有效起始日期");

        DocDefH activeVersion = getActiveVersion(docDefHV.getDocType(), VersioningUtils.parseMajor(docDefHV.getVersion()));
        if(activeVersion!=null) {
            //Assert.isTrue(activeVersion.getVersion().compareTo(docDefHV.getVersion())<0,
            //        String.format("当前版本比激活版本[%s]低，不允许激活",activeVersion.getVersion()));
            doDelete(activeVersion,true);
        }

        docDefHV.setState("Active");
        return doUpdate(docDefHV,false);
    }

    @Override
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
        redisSupport.delete(String.format(Constants.CACHE_DEF_DOC,docDefHV.getDocType(),docDefHV.getVersion()));
    }

    /**
     * 保存配置
     * @param docDefHV
     * @param force
     * @return
     */
    @Override
    public DocDefHV doUpdate(DocDefHV docDefHV, boolean force){

        Assert.isTrue(Pattern.matches("^[A-Z0-9]{4}$",docDefHV.getDocType()),"单据类型必须为A-Z以及0-9组成的4位字符串");

        NKDocProcessor  docProcessor        = customObjectManager.getCustomObject(docDefHV.getRefObjectType(), NKDocProcessor.class);
        DocDefH         lastUpdatedVersion  = getLastUpdatedVersion(docDefHV.getDocType(),docDefHV.getVersion());

        // 数据有效性检查
        if(StringUtils.isBlank(docDefHV.getVersion())){
            // 如果版本号为空，表示新创建的一个单据类型，需要检查是否已存在
            Assert.isTrue(force  || lastUpdatedVersion==null, String.format("单据类型%s已存在",docDefHV.getDocType()));
            docDefHV.setVersion("1.0.0");
        }else if(lastUpdatedVersion!=null){
            Assert.isTrue(StringUtils.equals(lastUpdatedVersion.getDocClassify(),docProcessor.classify().name()),"对象扩展分类不一致");
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

        redisSupport.delete(String.format(Constants.CACHE_DEF_DOC,docDefHV.getDocType(),docDefHV.getVersion()));

        return docDefHV;
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

    /**
     * 根据单据类型 获取当前日期下 单据对应的配置信息
     * @param docType docType
     * @return DocDefHV
     */
    @Override
    public DocDefHV getDocDef(String docType, Integer major){

        String version = null;

        String today = DateTimeUtilz.todayShortString();
        if(major == null){
            // 查找最新版本

            DocDefHExample example = new DocDefHExample();
            example.createCriteria()
                    .andDocTypeEqualTo(docType)
                    .andValidFromLessThanOrEqualTo(today)
                    .andValidToGreaterThanOrEqualTo(today)
                    .andStateEqualTo("Active");
            example.setOrderByClause("VERSION DESC");

            Optional<DocDefH> first = docDefHMapper
                    .selectByExample(example, new RowBounds(0, 1))
                    .stream()
                    .findFirst();

            Assert.isTrue(first.isPresent(),String.format("单据类型[%s]的配置没有找到",docType));

            version = first.get().getVersion();
        }else{
            DocDefHExample example = new DocDefHExample();
            example.createCriteria()
                    .andDocTypeEqualTo(docType)
                    .andVersionLike(major+".%")
                    .andValidFromLessThanOrEqualTo(today)
                    .andValidToGreaterThanOrEqualTo(today)
                    .andStateEqualTo("Active");

            Optional<DocDefH> first = docDefHMapper
                    .selectByExample(example, new RowBounds(0, 1))
                    .stream()
                    .findFirst();

            Assert.isTrue(first.isPresent(),String.format("单据类型[%s]版本[%s]的配置没有找到",docType,major));
        }

        return getDocDef(docType,version, false,false);
    }

    /**
     * 根据单据类型及版本号 获取单据对应的配置信息
     * @param docType docType
     * @param version version
     * @return DocDefHV
     */
    @Override
    public DocDefHV getDocDef(String docType, String version, boolean includeComponentMarkdown, boolean ignoreError){
        String cacheKey = String.format(Constants.CACHE_DEF_DOC,docType,version);

        DocDefHV docDefHV = redisSupport.getIfAbsent(cacheKey,StringUtils.EMPTY,()->{

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
            def.setFlows(docDefFlowMapper.selectByExample(flowExample));

            // cards
            DocDefIExample docDefIExample = new DocDefIExample();
            docDefIExample.createCriteria()
                    .andDocTypeEqualTo(docType)
                    .andVersionEqualTo(version);
            docDefIExample.setOrderByClause("ORDER_BY");
            def.setCards(BeanUtilz.copyFromList(docDefIMapper.selectByExampleWithBLOBs(docDefIExample),DocDefIV.class,(item)->{
                item.setConfig(JSON.parse(item.getCardContent()));
                item.setCardContent(null);

                Optional.ofNullable(customObjectManager.getCustomObjectIfExists(item.getCardHandler(),NKCard.class))
                        .ifPresent(nkCard -> {
                            item.setDataComponentName(nkCard.getDataComponentName());
                            item.setDefComponentNames(nkCard.getDefComponentNames());
                        });
            }));

            return def;
        });

        if(!ignoreError)
            Assert.notEmpty(docDefHV.getStatus(),String.format("单据类型[%s]的状态配置没有找到",docType));

        return docDefHV;
    }

    @Override
    public DocDefHV getDocDefinedRuntime(String docType, DocHV doc) {
        return null;
    }

//    @Override
//    public DocDefHV getDocDefinedRuntime(String docType, BizDoc doc){

//        DocDefHV defined = doc!=null?getDocDefined(doc.getDocType(), doc.getDefVersion(), true, false,false)
//                                        :getDocDefined(docType);
//        String runtimeState =  doc!=null?doc.getDocState()
//                                        :defined.getStatus()
//                                            .stream()
//                                            .filter(state->StringUtils.equals(state.getPreDocState(),Constants.BIZ_DEFAULT_EMPTY))
//                                            .findAny()
//                                            .orElseThrow(IllegalArgumentException::new)
//                                            .getDocState();
//
//        defined.setStatus(
//                defined.getStatus().stream()
//                        .map(state->BeanUtilz.copyFromObject(state, DefDocStatusExt.class))
//                        .peek(state->state.setAvailable(StringUtils.equalsAny(
//                                runtimeState,
//                                state.getPreDocState(),
//                                state.getDocState())))
//                        //.distinct()
//                        .collect(Collectors.toList())
//        );
//
//        BizDoc cxt = doc!=null?doc:new BizDoc();
//        defined.getCustomComponents()
//                .forEach(defDocComponentBO -> {
//                    defDocComponentBO.setWriteable(true);
//                    if(StringUtils.isNotBlank(defDocComponentBO.getEditableSpEL())){
//                        try {
//                            defDocComponentBO.setWriteable((Boolean) parser.parseExpression(defDocComponentBO.getEditableSpEL()).getValue(cxt));
//                        }catch (Exception e){
//                            throw new TfmsIllegalContentException("SpEL表达式["+defDocComponentBO.getEditableSpEL()+"]错误:"+e.getMessage());
//                        }
//                    }
//                });
//        return defined;
//    }

    @Override
    public void doInCards(DocDefHV docDefHV, RunInComponents runInComponents) throws Exception{

        for(DocDefIV docDefI : docDefHV.getCards()){
            // 找到对应的组件实现类
            runInComponents.run(
                    customObjectManager.getCustomObjectIfExists(docDefI.getCardKey(), NKCard.class),
                    docDefI);
        }
    }
//
//    @Override
//    public int deployOrder() {
//        return Integer.MIN_VALUE + 1;
//    }
//
//    @Override
//    public Object deployExport(JSONObject config) {
//        JSONArray array = config.getJSONArray("docTypes");
//        if(array!=null){
//            return array.stream()
//                            .map(docType-> getDocDefined(docType.toString(),1,
//                                    true,true,false))
//                            .collect(Collectors.toList());
//        }
//        return Collections.emptyList();
//    }
//
//    @Override
//    public void deployImport(Object data) {
//        if(data!=null)
//            ((JSONArray)data).toJavaList(DocDefHV.class)
//                .forEach(DocDefHV -> doUpdate(DocDefHV,true,true));
//    }


    private DocDefH getActiveVersion(String docType,String major){
        DocDefHExample example = new DocDefHExample();
        example.createCriteria()
                .andDocTypeEqualTo(docType)
                .andStateEqualTo("Active")
                .andVersionLike(major+".%");
        example.setOrderByClause("VERSION desc");

        return docDefHMapper.selectByExample(example, new RowBounds(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
    }
    /**
     * 获取同一个Major版本的最后一次更新
     * @param docType
     * @param versionPrefix
     * @return
     */
    private DocDefH getLastUpdatedVersion(String docType,String versionPrefix){

        String major = StringUtils.isBlank(versionPrefix)?"%":(versionPrefix + ".%");

        DocDefHExample example = new DocDefHExample();
        example.createCriteria()
                .andDocTypeEqualTo(docType)
                .andVersionLike(major);
        example.setOrderByClause("VERSION desc");

        return docDefHMapper.selectByExample(example, new RowBounds(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
    }
}
