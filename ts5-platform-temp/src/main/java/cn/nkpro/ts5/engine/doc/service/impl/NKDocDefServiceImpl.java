package cn.nkpro.ts5.engine.doc.service.impl;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.basic.NKCustomObjectManager;
import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.config.mybatis.pagination.PaginationContext;
import cn.nkpro.ts5.engine.doc.NKCard;
import cn.nkpro.ts5.engine.doc.NKDocProcessor;
import cn.nkpro.ts5.engine.doc.NKDocStateInterceptor;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.doc.service.NKDocDefService;
import cn.nkpro.ts5.exception.TfmsComponentException;
import cn.nkpro.ts5.model.mb.gen.*;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by bean on 2020/6/10.
 */
@Slf4j
@Service
public class NKDocDefServiceImpl implements NKDocDefService {

    private ExpressionParser parser = new SpelExpressionParser();
    @Autowired
    private DocDefHMapper docDefHMapper;
    @Autowired
    private DefDocStatusMapper defDocStatusMapper;
    @Autowired
    private DocDefIMapper docDefIMapper;
    @Autowired
    private RedisSupport<DocDefHV> redisSupport;
    @Autowired
    private RedisSupport<Object> redisSupportObject;
    @Autowired
    private NKCustomObjectManager customObjectManager;

    @Override
    public PageList<DocDefH> getPage(String docType, String keyword, int from, int rows, String orderField, String order){

        DocDefHExample example = new DocDefHExample();

        DocDefHExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(docType))
            criteria.andDocTypeEqualTo(docType);
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
    public List<DocDefH> getDocTypes(){
        DocDefHExample example = new DocDefHExample();
        example.createCriteria().andVersionHeadEqualTo(1);
        example.setOrderByClause("DOC_TYPE");

        return docDefHMapper.selectByExample(example);
    }


    @Override
    public Map<String, Object> options(){
        Map<String,Object> options = new HashMap<>();
        //options.put("docInterceptors",customObjectManager.getCustomObjectDescriptionList());
        options.put("docStateInterceptors",customObjectManager.getInterceptorNames(NKDocStateInterceptor.class,true));
        //options.put("components",customObjectManager.getCustomComponents());
        return options;
    }

    @Override
    public void doUpdate(DocDefHV docDefHV, boolean create,boolean force){

        DocDefH exists = docDefHMapper.selectByPrimaryKey(docDefHV);
        Assert.isTrue(force || !create || exists==null,
                String.format("单据类型%s已存在",docDefHV.getDocType()));

        Assert.isTrue(Pattern.matches("^[A-Z0-9]{4}$",docDefHV.getDocType()),
                "单据类型必须为A-Z以及0-9组成的4位字符串");

        NKDocProcessor docProcessor = customObjectManager.getCustomObject(docDefHV.getRefObjectType(), NKDocProcessor.class);

        if(!create){
            Assert.isTrue(StringUtils.equals(exists.getDocClassify(),docProcessor.classify().name()),"对象扩展分类不一致");
        }

        // status
        DefDocStatusExample docStatusExample = new DefDocStatusExample();
        docStatusExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        defDocStatusMapper.deleteByExample(docStatusExample);
        docDefHV.getStatus()
                .forEach(state->{

                    state.setDocType(docDefHV.getDocType());
                    state.setVersion(docDefHV.getVersion());
                    state.setOrderby(docDefHV.getStatus().indexOf(state));
                    state.setUpdatedTime(DateTimeUtilz.nowSeconds());

                    defDocStatusMapper.insertSelective(state);
                });

        // components
        DocDefIExample docComponentExample = new DocDefIExample();
        docComponentExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        docDefIMapper.deleteByExample(docComponentExample);
        docDefHV.getItems()
                .forEach(item -> {

                    item.setDocType(docDefHV.getDocType());
                    item.setVersion(docDefHV.getVersion());
                    item.setUpdatedTime(DateTimeUtilz.nowSeconds());
                    item.setOrderBy(docDefHV.getItems().indexOf(item));
                    item.setMarkdownFlag(StringUtils.isNotBlank(item.getMarkdown())?1:0);
                    docDefIMapper.insertSelective(item);
                });

        // 循环调用组件
        log.debug("对单据数据进行反序列化");
        doInCards(docDefHV,(card, docDefI)-> {

            Assert.isTrue(card!=null,String.format("组件实现类[%s]不存在",docDefI.getItemKey()));

            Object componentData = docDefHV.getItemDefs().get(docDefI.getItemKey());
            try {
                docDefHV.getItemDefs().put(
                        docDefI.getItemKey(),
                        card.deserializeDef(componentData)
                );
            } catch (Exception e) {
                throw new TfmsComponentException(docDefI.getItemKey(), e);
            }
        });

        //DocDefHV.setRefObjectTypeOptions(JSON.toJSONString(DocDefHV.getDocDef()));
        docDefHV.setMarkdownFlag(StringUtils.isBlank(docDefHV.getMarkdown())?0:1);

        // 单据
        docDefHV.setUpdatedTime(DateTimeUtilz.nowSeconds());
        if(exists==null){
            docDefHMapper.insertSelective(docDefHV);
        }else{
            docDefHMapper.updateByPrimaryKeySelective(docDefHV);
        }

        redisSupport.delete(String.format(Constants.CACHE_DEF_DOC,docDefHV.getDocType(),docDefHV.getVersion()));
    }

//    @Override
//    public String getDocComponentDefMarkdown(DefDocComponentKey key){
//
//        if(StringUtils.isNotBlank(key.getComponent())){
//            DefDocComponent docComponent = defDocComponentMapper.selectByPrimaryKey(key);
//            if(docComponent!=null){
//                return docComponent.getMarkdown();
//            }
//        }else if(StringUtils.isNotBlank(key.getDocType())){
//            DocDefHV docType = DocDefHVMapper.selectByPrimaryKey(BeanUtilz.copyFromObject(key, DocDefHVKey.class));
//            if(docType!=null){
////                if(StringUtils.equals(EnumDocClassify.PROJECT.name(),docType.getDocClassify())){
////                    DefProjectDocExample example = new DefProjectDocExample();
////                    example.createCriteria().andProjectTypeEqualTo(docType.getDocType());
////                    defProjectDocMapper.selectByExample()
////                }
//                return docType.getMarkdown();
//            }
//        }else{
//            DocDefHVExample example = new DocDefHVExample();
//            example.createCriteria()
//                    //.andHeadVersionEqualTo(1)
//                    //.andMarkdownFlagEqualTo(1)
//            ;
//            example.setOrderByClause("DOC_TYPE");
//            List<DocDefHV> list = DocDefHVMapper.selectByExample(example);
//
//            StringBuilder builder = new StringBuilder();
//            builder.append("\n\n### 业务 \n");
//            list.stream()
//                    .filter(type->StringUtils.equals(type.getDocClassify(),EnumDocClassify.PROJECT.name()))
//                    .forEach(type->{
//                        builder.append(String.format("* [%s | %s](nkdn://user/%s/%s)",type.getDocType(),type.getDocName(),type.getDocType(),type.getVersion()));
//                        builder.append('\n');
//                    });
//            builder.append("\n\n### 交易 \n");
//            list.stream()
//                    .filter(type->StringUtils.equals(type.getDocClassify(),EnumDocClassify.TRANSACTION.name()))
//                    .forEach(type->{
//                        builder.append(String.format("* [%s | %s](nkdn://user/%s/%s)",type.getDocType(),type.getDocName(),type.getDocType(),type.getVersion()));
//                        builder.append('\n');
//                    });
//            builder.append("\n\n### 伙伴角色 \n");
//            list.stream()
//                    .filter(type->StringUtils.equals(type.getDocClassify(),EnumDocClassify.PARTNER.name()))
//                    .forEach(type->{
//                        builder.append(String.format("* [%s | %s](nkdn://user/%s/%s)",type.getDocType(),type.getDocName(),type.getDocType(),type.getVersion()));
//                        builder.append('\n');
//                    });
//            builder.append("\n\n### 伙伴单据 \n");
//            list.stream()
//                    .filter(type->StringUtils.equals(type.getDocClassify(),EnumDocClassify.PARTNER_T.name()))
//                    .forEach(type->{
//                        builder.append(String.format("* [%s | %s](nkdn://user/%s/%s)",type.getDocType(),type.getDocName(),type.getDocType(),type.getVersion()));
//                        builder.append('\n');
//                    });
//
//            builder.append('\n').append('\n').append('\n').append('\n').append('\n').append('\n');
//
//            return builder.toString();
//        }
//        return null;
//    }

    /**
     * 根据单据类型 获取当前日期下 单据对应的配置信息
     * @param docType docType
     * @return DocDefHV
     */
    @Override
    public DocDefHV getDocDefined(String docType){

        String today = DateTimeUtilz.todayShortString();

        DocDefHExample example = new DocDefHExample();
        example.createCriteria()
                .andDocTypeEqualTo(docType)
                .andValidFromLessThanOrEqualTo(today)
                .andValidToGreaterThanOrEqualTo(today);
        example.setOrderByClause("VERSION DESC");

        Optional<DocDefH> first = docDefHMapper
                .selectByExample(example, new RowBounds(0, 1))
                .stream()
                .findFirst();

        Assert.isTrue(first.isPresent(),String.format("单据类型[%s]的配置没有找到",docType));

        return getDocDefined(docType,first.get().getVersion(),true, false,false);
    }

    /**
     * 根据单据类型及版本号 获取单据对应的配置信息
     * @param docType docType
     * @param version version
     * @return DocDefHV
     */
    @Override
    public DocDefHV getDocDefined(String docType,Integer version, boolean includeComponentDef, boolean includeComponentMarkdown, boolean ignoreError){
        String cacheKey = String.format(Constants.CACHE_DEF_DOC,docType,version);

        DocDefHV docDefHV = redisSupport.getIfAbsent(cacheKey,StringUtils.EMPTY,()->{

            // todo 暂时只考虑了type的版本管理，子配置表都没有版本管理
            DocDefHKey key = new DocDefHKey();
            key.setDocType(docType);
            key.setVersion(version);

            DocDefH docDefH = docDefHMapper.selectByPrimaryKey(key);
            docDefH.setMarkdown(null);
            Assert.notNull(docDefH,String.format("单据类型[%s]的配置没有找到",docType));

            DocDefHV def = BeanUtilz.copyFromObject(docDefH,DocDefHV.class);

            // status
            DefDocStatusExample statusExample = new DefDocStatusExample();
            statusExample.createCriteria()
                    .andDocTypeEqualTo(docType)
                    .andVersionEqualTo(version);
            statusExample.setOrderByClause("ORDERBY");
            def.setStatus(defDocStatusMapper.selectByExample(statusExample));

            // components
            DocDefIExample docDefIExample = new DocDefIExample();
            docDefIExample.createCriteria()
                    .andDocTypeEqualTo(docType)
                    .andVersionEqualTo(version);
            docDefIExample.setOrderByClause("ORDER_BY");
            def.setItems(docDefIMapper.selectByExampleWithBLOBs(docDefIExample));


            return def;
        });

        NKDocProcessor docProcessor = customObjectManager.getCustomObject(docDefHV.getRefObjectType(), NKDocProcessor.class);

        //docDefHV.setDocHeaderComponent(docProcessor.getDocHeaderComponentName());
        //docDefHV.setDocDefNames(docProcessor.getDocDefComponentNames());
        //docDefHV.setDocDef(docProcessor.getDocProcessDef(docDefHV));

//        if(includeComponentMarkdown){
//            DefDocComponentExample componentExample = new DefDocComponentExample();
//            componentExample.createCriteria()
//                    .andDocTypeEqualTo(docType)
//                    .andVersionEqualTo(version);
//            componentExample.setOrderByClause("ORDERBY");
//            DocDefHV.setCustomComponents(BeanUtilz.copyFromList(defDocComponentMapper.selectByExampleWithBLOBs(componentExample), DefDocComponentBO.class));
//        }

        if(includeComponentDef){

            List<String> hashKeys = new ArrayList<>();
            doInCards(docDefHV,(cardHandler, docDefI) -> {
                if (cardHandler != null) {
                    hashKeys.add(cardHandler.getCardHandler());
                }
            });
            Map<String, Object> objectHash = redisSupportObject.getHash(cacheKey, hashKeys);

//            // 循环加载自定义组件配置
//            doInCards(docDefHV,(cardHandler, docDefI) -> {
//
//                if(cardHandler!=null){
//                    docDefI.setComponentName(StringUtils.defaultIfBlank(docDefI.getComponentName(),cardHandler.getComponentDesc()));
//
//                    if(cardHandler instanceof NKCardComponent) {
//                        NKCardComponent cardComponent = (NKCardComponent) cardHandler;
//                        docDefI.setDataComponent(cardComponent.getDataComponentName());
//                        docDefI.setDataComponentExtNames(cardComponent.getDataComponentExtNames());
//                        docDefI.setPageComponentNames(cardComponent.getPageComponentNames());
//                    }
//                    docDefI.setDefComponentNames(cardHandler.getDefComponentNames());
//
//                    Object def;
//                    if(cardHandler.cacheDef()){
//                        def = objectHash.computeIfAbsent(cardHandler.getComponentName(), (key) -> {
//                            // 执行组件getData方法
//                            Object object;
//                            try {
//                                object = cardHandler.getDef(DocDefHV);
//                            } catch (Exception e) {
//                                throw new TfmsComponentException(cardHandler.getComponentName(), e);
//                            }
//                            //object = object == null? Collections.EMPTY_MAP:object;
//                            redisSupportObject.putHash(cacheKey, key, object);
//
//                            return object;
//                        });
//                    }else{
//                        try {
//                            def = cardHandler.getDef(DocDefHV);
//                        } catch (Exception e) {
//                            throw new TfmsComponentException(cardHandler.getComponentName(), e);
//                        }
//                    }
//
//                    DocDefHV.getCustomComponentsDef().put(
//                            cardHandler.getComponentName(),
//                            def
//                    );
//                }else{
//                    docDefI.setComponentName("<组件对象缺失，请检查配置>");
//                }
//            });

        }

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

    private void doInCards(DocDefHV docDefHV, RunInComponents runInComponents){

        docDefHV.getItems().forEach((docDefI) -> {
            // 找到对应的组件实现类
            runInComponents.run(
                    customObjectManager.getCustomObjectIfExists(docDefI.getItemKey(), NKCard.class),
                    docDefI);
        });
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

    @FunctionalInterface
    private interface RunInComponents{
        void run(NKCard card, DocDefI docDefI);
    }
}
