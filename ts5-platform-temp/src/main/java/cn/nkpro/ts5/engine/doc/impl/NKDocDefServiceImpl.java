package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.basic.NKCustomObject;
import cn.nkpro.ts5.basic.NKCustomObjectManager;
import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.config.mybatis.pagination.PaginationContext;
import cn.nkpro.ts5.engine.doc.NKCard;
import cn.nkpro.ts5.engine.doc.NKDocProcessor;
import cn.nkpro.ts5.engine.doc.NKDocStateInterceptor;
import cn.nkpro.ts5.engine.doc.model.CardDescribe;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocDefIV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.doc.service.NKDocDefService;
import cn.nkpro.ts5.model.mb.gen.*;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
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

/**
 * Created by bean on 2020/6/10.
 */
@Slf4j
@Service
public class NKDocDefServiceImpl implements NKDocDefService {

    @Autowired
    private DocDefHMapper docDefHMapper;
    @Autowired
    private DocDefIMapper docDefIMapper;
    @Autowired
    private DocDefStateMapper docDefStateMapper;
    @Autowired
    private RedisSupport<DocDefHV> redisSupport;
    @Autowired
    private RedisSupport<Object> redisSupportObject;
    @Autowired
    private NKCustomObjectManager customObjectManager;

    @Override
    public PageList<DocDefH> getPage(String docClassify, String docType, String keyword, int from, int rows, String orderField, String order){

        DocDefHExample example = new DocDefHExample();

        DocDefHExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(docType))
            criteria.andDocClassifyEqualTo(docClassify);
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
    public CardDescribe getCardDescribe(String cardHandlerName){
        return BeanUtilz.copyFromObject(customObjectManager.getCustomObject(cardHandlerName,NKCard.class), CardDescribe.class);
    }

    @Override
    public void doUpdate(DocDefHV docDefHV, boolean create,boolean force){

        /*
         * 获取修改前的详情
         */
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
        DocDefStateExample stateExample = new DocDefStateExample();
        stateExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        docDefStateMapper.deleteByExample(stateExample);
        docDefHV.getStatus()
                .forEach(state->{

                    state.setDocType(docDefHV.getDocType());
                    state.setVersion(docDefHV.getVersion());
                    state.setOrderBy(docDefHV.getStatus().indexOf(state));
                    state.setUpdatedTime(DateTimeUtilz.nowSeconds());

                    docDefStateMapper.insertSelective(state);
                });

        // components
        DocDefIExample defIExample = new DocDefIExample();
        defIExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        docDefIMapper.deleteByExample(defIExample);
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

        // 单据
        docDefHV.setDocClassify(docProcessor.classify().name());
        docDefHV.setMarkdownFlag(StringUtils.isBlank(docDefHV.getMarkdown())?0:1);
        docDefHV.setUpdatedTime(DateTimeUtilz.nowSeconds());
        if(exists==null){
            docDefHMapper.insertSelective(docDefHV);
        }else{
            docDefHMapper.updateByPrimaryKeySelective(docDefHV);
        }

        redisSupport.delete(String.format(Constants.CACHE_DEF_DOC,docDefHV.getDocType(),docDefHV.getVersion()));
    }

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

    private void doInCards(DocDefHV docDefHV, RunInComponents runInComponents){

        docDefHV.getCards().forEach((docDefI) -> {
            // 找到对应的组件实现类
            runInComponents.run(
                    customObjectManager.getCustomObjectIfExists(docDefI.getCardKey(), NKCard.class),
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
        void run(NKCard card, DocDefIV docDefIV);
    }
}
