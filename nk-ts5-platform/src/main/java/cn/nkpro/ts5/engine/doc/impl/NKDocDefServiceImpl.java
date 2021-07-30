package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.config.mybatis.pagination.PaginationContext;
import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.engine.co.DebugContextManager;
import cn.nkpro.ts5.engine.co.NKCustomObject;
import cn.nkpro.ts5.engine.co.NKCustomObjectManager;
import cn.nkpro.ts5.engine.doc.NKCard;
import cn.nkpro.ts5.engine.doc.NKDocProcessor;
import cn.nkpro.ts5.engine.doc.NKDocStateInterceptor;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocDefIV;
import cn.nkpro.ts5.engine.doc.service.NKDocDefService;
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
public class NKDocDefServiceImpl implements NKDocDefService {

    @Autowired@SuppressWarnings("all")
    private RedisSupport<DocDefHV> redisSupport;
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
    public List<DocDefH> getList(String docType, int page){
        DocDefHExample example = new DocDefHExample();
        example.createCriteria()
                .andDocTypeEqualTo(docType);
        example.setOrderByClause("UPDATED_TIME DESC");
        return docDefHMapper.selectByExample(example, new RowBounds((page-1)*10, 10));
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

        NKCard nkCard = customObjectManager.getCustomObject(cardHandlerName, NKCard.class);

        DocDefIV describe = new DocDefIV();
        describe.setCardHandler(nkCard.getCardHandler());
        describe.setCardName(nkCard.getCardName());
        describe.setDataComponentName(nkCard.getDataComponentName());
        describe.setDefComponentNames(nkCard.getDefComponentNames());

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
        NKDocProcessor docProcessor = customObjectManager.getCustomObject(docDefHV.getRefObjectType(), NKDocProcessor.class);

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
                    flow.setActive(StringUtils.equals(docDefHV.getState(),"Active")?1:0);
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
        DocDefH record = new DocDefH();
        record.setState("History");
        DocDefHExample example = new DocDefHExample();
        example.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andStateEqualTo("Active");
        docDefHMapper.updateByExampleSelective(record, example);

        docDefHV.setState("Active");

        docDefHV = doUpdate(docDefHV,false);
        redisSupport.delete(Constants.CACHE_DEF_DOC,docDefHV.getDocType());

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
            (DocDefHV)(debugContextManager.getDebugResources(String.format("@%s", docType)))
        ).orElse(
            redisSupport.getIfAbsent(Constants.CACHE_DEF_DOC,docType,()-> getLastActiveVersion(docType))
        );
        Assert.notNull(defHV,String.format("单据类型[%s]版本的配置没有找到或尚未激活",docType));


        deserializeDef(defHV);

        runLoopCards(defHV,false, (nkCard,item)->
                item.setConfig(nkCard.afterGetDef(defHV, item, item.getConfig()))
        );

        DocDefFlowExample flowExample = new DocDefFlowExample();
        flowExample.createCriteria()
                .andPreDocTypeEqualTo(docType)
                .andActiveEqualTo(1);
        flowExample.setOrderByClause("ORDER_BY");
        defHV.setFlows(docDefFlowMapper.selectByExample(flowExample));

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
        DocDefHV docDefHV = debugContextManager.getDebugResources(String.format("@%s", docType));
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
        def.setFlows(docDefFlowMapper.selectByExample(flowExample));

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
            item.setDefComponentNames(nkCard.getDefComponentNames());
            item.setCardContent(null);
        });

        return docDefHV;
    }

    /**
     * 验证配置是否可运行
     * @param docDefHV docDefHV
     */
    private void validateDef(DocDefHV docDefHV){

        customObjectManager.getCustomObject(docDefHV.getRefObjectType(),NKCustomObject.class);

        runLoopCards(docDefHV,false,(card, docDefIV)->{

        });
    }

    @Override
    public void runLoopCards(DocDefHV docDefHV, boolean ignoreError, Function function){
        for(DocDefIV docDefI : docDefHV.getCards()){
            // 找到对应的组件实现类
            NKCard nkCard = customObjectManager.getCustomObjectIfExists(docDefI.getCardKey(), NKCard.class);
            if(nkCard==null && !ignoreError){
                throw new TfmsException(String.format("自定义对象[%s]不存在",docDefI.getCardHandler()));
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
