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
import cn.nkpro.elcube.co.*;
import cn.nkpro.elcube.data.mybatis.pagination.PaginationContext;
import cn.nkpro.elcube.data.redis.RedisSupport;
import cn.nkpro.elcube.docengine.NkCard;
import cn.nkpro.elcube.docengine.NkDocEngineThreadLocal;
import cn.nkpro.elcube.docengine.NkDocProcessor;
import cn.nkpro.elcube.docengine.datasync.NkDocDataAdapter;
import cn.nkpro.elcube.docengine.gen.*;
import cn.nkpro.elcube.docengine.interceptor.NkDocCycleInterceptor;
import cn.nkpro.elcube.docengine.interceptor.NkDocFlowInterceptor;
import cn.nkpro.elcube.docengine.interceptor.NkDocStateInterceptor;
import cn.nkpro.elcube.docengine.model.DocDefFlowV;
import cn.nkpro.elcube.docengine.model.DocDefHV;
import cn.nkpro.elcube.docengine.model.DocDefIV;
import cn.nkpro.elcube.docengine.model.DocDefStateV;
import cn.nkpro.elcube.docengine.service.NkDocDefService;
import cn.nkpro.elcube.exception.NkDefineException;
import cn.nkpro.elcube.platform.DeployAble;
import cn.nkpro.elcube.security.SecurityUtilz;
import cn.nkpro.elcube.utils.BeanUtilz;
import cn.nkpro.elcube.utils.DateTimeUtilz;
import cn.nkpro.elcube.utils.UUIDHexGenerator;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/6/10.
 */
@Order(50)
@Slf4j
@Service
public class NkDocDefServiceImpl implements NkDocDefService, DeployAble {

    @Autowired
    private RedisSupport<DocDefHV> redisSupport;
    @Autowired
    private RedisSupport<List<DocDefFlowV>> redisSupportFlows;
    @Autowired
    private DebugContextManager debugContextManager;
    @Autowired
    private NkCustomObjectManager customObjectManager;

    @Autowired
    private DocDefHMapper docDefHMapper;
    @Autowired
    private DocDefIMapper docDefIMapper;
    @Autowired
    private DocDefStateMapper docDefStateMapper;
    @Autowired
    private DocDefFlowMapper docDefFlowMapper;
    @Autowired
    private DocDefCycleMapper docDefCycleMapper;
    @Autowired
    private DocDefIndexRuleMapper docDefIndexRuleMapper;
    @Autowired
    private DocDefIndexCustomMapper docDefIndexCustomMapper;
    @Autowired
    private DocDefDataSyncMapper docDefDataSyncMapper;
    @Autowired
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
        options.put("docCycleInterceptor",      customObjectManager.getCustomObjectDescriptionList(NkDocCycleInterceptor.class,     true,null));
        options.put("docFlowInterceptors",      customObjectManager.getCustomObjectDescriptionList(NkDocFlowInterceptor.class,      true,null));
        options.put("docDataSyncAdapters",      customObjectManager.getCustomObjectDescriptionList(NkDocDataAdapter.class,          false,null));
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
    public DocDefHV doRun(DocDefHV docDefHV, boolean run){
        if(run){
            Assert.isTrue(StringUtils.equals(docDefHV.getState(),"InActive"),"??????????????????????????????");
            validateDef(docDefHV);
            doUpdate(docDefHV, false);

            // ???????????????????????????????????????????????????json???????????????????????????????????????debug???????????????????????????????????????
            docDefHV.getCards().forEach(card->card.setConfig(null));

            debugContextManager.addDebugResource(String.format("@%s",docDefHV.getDocType()),docDefHV);
        }else {
            debugContextManager.removeDebugResource(String.format("@%s",docDefHV.getDocType()),docDefHV);
        }
        return deserializeDefFromContent(docDefHV);
    }

    /**
     * ???????????????????????????Major
     * @param docDefHV DocDefHV
     * @return DocDefHV
     */
    @Override
    @Transactional
    public DocDefHV doBreach(DocDefHV docDefHV){

        // ??????????????????????????????????????????????????? ??????Minor
        docDefHV.setVersion(UUIDHexGenerator.generate());
        docDefHV.setState("InActive");

        return doUpdate(docDefHV,false);
    }

    /**
     * ????????????
     * @param docDefHV DocDefHV
     * @param force force
     * @return DocDefHV
     */
    @Override
    @Transactional
    public DocDefHV doUpdate(DocDefHV docDefHV, boolean force){

        Assert.isTrue(Pattern.matches("^[A-Z0-9]{4}$",docDefHV.getDocType()),"?????????????????????A-Z??????0-9?????????4????????????");


        // ?????????????????????
        DocDefH lastUpdatedVersion  = getLastUpdatedVersion(docDefHV.getDocType());
        NkDocProcessor docProcessor = customObjectManager.getCustomObject(docDefHV.getRefObjectType(), NkDocProcessor.class);

        if(lastUpdatedVersion!=null){
            Assert.isTrue(StringUtils.equals(lastUpdatedVersion.getDocClassify(),docProcessor.classify().name()),"???????????????????????????");
        }

        if(StringUtils.isBlank(docDefHV.getVersion())){
            // ??????????????????????????????????????????????????????????????????????????????????????????
            Assert.isTrue(force  || lastUpdatedVersion==null, String.format("????????????%s?????????",docDefHV.getDocType()));
            docDefHV.setVersion(UUIDHexGenerator.generate());
        }

        deserializeDef(docDefHV);

        // status
        Assert.notEmpty(docDefHV.getStatus(),"??????????????????");
        DocDefStateExample stateExample = new DocDefStateExample();
        stateExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        docDefStateMapper.deleteByExample(stateExample);
        if(docDefHV.getStatus()!=null)
            docDefHV.getStatus()
                .forEach(state->{
                    Assert.hasLength(state.getDocState(),"?????? ????????????");
                    Assert.hasLength(state.getDocStateDesc(),"???????????? ????????????");

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
        if(docDefHV.getLifeCycles()!=null)
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
        if(docDefHV.getBpms()!=null)
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
        Assert.notEmpty(docDefHV.getFlows(),"?????????????????????");
        DocDefFlowExample flowExample = new DocDefFlowExample();
        flowExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        docDefFlowMapper.deleteByExample(flowExample);
        if(docDefHV.getFlows()!=null)
            docDefHV.getFlows()
                .forEach(flow->{
                    Assert.hasLength(flow.getPreDocType(),"????????? ???????????? ????????????");
                    flow.setPreDocState(StringUtils.defaultIfBlank(flow.getPreDocState(),"@"));
                    flow.setDocType(docDefHV.getDocType());// ????????????????????????????????????
                    flow.setDocName(docDefHV.getDocName());// ????????????????????????????????????
                    flow.setClassify(docDefHV.getDocClassify());
                    flow.setVersion(docDefHV.getVersion());
                    flow.setState(docDefHV.getState());
                    flow.setOrderBy(docDefHV.getFlows().indexOf(flow));
                    flow.setUpdatedTime(DateTimeUtilz.nowSeconds());
                    docDefFlowMapper.insert(flow);
                });

        // indexRule
        DocDefIndexRuleExample indexRuleExample = new DocDefIndexRuleExample();
        indexRuleExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        docDefIndexRuleMapper.deleteByExample(indexRuleExample);
        if(docDefHV.getIndexRules()!=null)
            docDefHV.getIndexRules()
                    .forEach(indexRule -> {
                        Assert.hasLength(indexRule.getIndexName(),"?????? ????????? ????????????");
                        Assert.hasLength(indexRule.getIndexType(),"?????? ????????? ????????????");
                        Assert.hasLength(indexRule.getRuleSpEL(), "?????? ?????? ????????????");
                        indexRule.setDocType(docDefHV.getDocType());
                        indexRule.setVersion(docDefHV.getVersion());
                        indexRule.setOrderBy(docDefHV.getIndexRules().indexOf(indexRule));
                        indexRule.setUpdatedTime(DateTimeUtilz.nowSeconds());

                        docDefIndexRuleMapper.insert(indexRule);
                    });

        // indexCustom
        DocDefIndexCustomExample indexCustomExample = new DocDefIndexCustomExample();
        indexCustomExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        docDefIndexCustomMapper.deleteByExample(indexCustomExample);
        if(docDefHV.getIndexCustoms()!=null)
            docDefHV.getIndexCustoms()
                    .forEach(indexCustom -> {
                        Assert.hasLength(indexCustom.getCustomType(),   "??????????????? ???????????? ????????????");
                        Assert.hasLength(indexCustom.getDataSpEL(),     "??????????????? ?????????SpEL ????????????");
                        Assert.hasLength(indexCustom.getKeySpEL(),      "??????????????? ??????SpEL ????????????");
                        Assert.hasLength(indexCustom.getMappingSpEL(),  "??????????????? ?????????????????? ????????????");
                        indexCustom.setDocType(docDefHV.getDocType());
                        indexCustom.setVersion(docDefHV.getVersion());
                        indexCustom.setOrderBy(docDefHV.getIndexCustoms().indexOf(indexCustom));
                        indexCustom.setUpdatedTime(DateTimeUtilz.nowSeconds());

                        docDefIndexCustomMapper.insert(indexCustom);
                    });

        // dataSync
        DocDefDataSyncExample dataSyncExample = new DocDefDataSyncExample();
        dataSyncExample.createCriteria()
                .andDocTypeEqualTo(docDefHV.getDocType())
                .andVersionEqualTo(docDefHV.getVersion());
        docDefDataSyncMapper.deleteByExample(dataSyncExample);
        if(docDefHV.getDataSyncs()!=null)
            docDefHV.getDataSyncs()
                    .forEach(dataSync -> {
                        Assert.hasLength(dataSync.getTargetSvr(),    "???????????? ???????????? ????????????");
                        Assert.hasLength(dataSync.getDataSpEL(),     "???????????? ?????????SpEL ????????????");
                        Assert.hasLength(dataSync.getKeySpEL(),      "???????????? ??????SpEL ????????????");
                        Assert.hasLength(dataSync.getMappingSpEL(),  "???????????? ?????????????????? ????????????");
                        dataSync.setDocType(docDefHV.getDocType());
                        dataSync.setVersion(docDefHV.getVersion());
                        dataSync.setOrderBy(docDefHV.getDataSyncs().indexOf(dataSync));
                        dataSync.setUpdatedTime(DateTimeUtilz.nowSeconds());

                        docDefDataSyncMapper.insert(dataSync);
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
        docDefHV.setUpdatedAccount(SecurityUtilz.getUser().getUsername());
        if(docDefHMapper.selectByPrimaryKey(docDefHV)==null){
            docDefHV.setCreatedTime(DateTimeUtilz.nowSeconds());
            docDefHV.setCreatedAccount(SecurityUtilz.getUser().getUsername());
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
     * ?????????????????????Minor
     * @param docDefHV DocDefHV
     * @return DocDefHV
     */
    @Override
    @Transactional
    public DocDefHV doActive(DocDefHV docDefHV){

        Assert.hasText(docDefHV.getValidFrom(),"???????????????????????????");
        Assert.hasText(docDefHV.getValidTo(),  "???????????????????????????");

        validateDef(docDefHV);

        // ?????????????????????
        clearActiveVersion(docDefHV.getDocType());

        // ???????????????
        docDefHV.setState("Active");

        docDefHV = doUpdate(docDefHV,false);
        // ??????????????????????????????????????????
        redisSupport.deleteHash(Constants.CACHE_DEF_DOC_TYPES,docDefHV.getDocType());

        // ??????????????????????????????????????????????????????????????????????????????
        // redisSupport.delete(Constants.CACHE_DEF_DOC_FLOWS);

        // ????????????????????????????????????????????????
        DocDefHV def = docDefHV;
        TransactionSync.runBeforeCommit("?????????????????????",()->
            redisSupportFlows.set(Constants.CACHE_DEF_DOC_FLOWS, def.getDocType(), def.getFlows())
        );

        debugContextManager.removeDebugResource(String.format("@%s",docDefHV.getDocType()), docDefHV);

        return docDefHV;
    }

    @Override
    @Transactional
    public void doDelete(DocDefH docDefHV, boolean force){
        if(!force){
            Assert.isTrue(!docDefHV.getState().equals("Active"),"???????????????????????????");
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

        debugContextManager.removeDebugResource(String.format("@%s",docDefHV.getDocType()), docDefHV);
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
    public List<DocDefFlowV> getEntrance(String classify){

        return getDocTypeFlows("@")
                .stream()
                .filter(flowV->StringUtils.isBlank(classify) || StringUtils.equals(flowV.getClassify(),classify))
                .sorted(Comparator.comparing(DocDefFlowV::getDocType))
                .collect(Collectors.toList());
    }

    @Override
    public List<DocDefFlowV> getDocTypeFlows(String docType){
        // ?????????????????????????????????????????????
        Map<String, List<DocDefFlowV>> docTypeFlows = redisSupportFlows.getHashIfAbsent(Constants.CACHE_DEF_DOC_FLOWS, () -> {

            DocDefHExample docDefHExample = new DocDefHExample();
            docDefHExample.createCriteria()
                    .andStateEqualTo("Active");
            Map<String,DocDefH> docDefs = docDefHMapper.selectByExample(docDefHExample)
                    .stream()
                    .collect(Collectors.toMap(DocDefHKey::getDocType, e->e));

            DocDefFlowExample flowExample = new DocDefFlowExample();
            flowExample.createCriteria()
                    .andStateEqualTo("Active");
            flowExample.setOrderByClause("DOC_TYPE, ORDER_BY");

            Map<String, List<DocDefFlowV>> flows = new HashMap<>();

            docDefFlowMapper.selectByExample(flowExample)
                    .forEach(item ->{
                            DocDefFlowV flowV = BeanUtilz.copyFromObject(item,DocDefFlowV.class);
                            if(docDefs.containsKey(flowV.getDocType())){
                                DocDefH defH = docDefs.get(flowV.getDocType());
                                flowV.setClassify(defH.getDocClassify());   // ????????????????????????????????????
                                flowV.setDocName(defH.getDocName());        // ????????????????????????????????????
                                flows.computeIfAbsent(item.getDocType(),(key) -> new ArrayList<>()).add(flowV);
                            }
                    });

            return flows;
        });

        // ?????????????????????????????????????????????????????????
        List<DocDefHV> debugResources = debugContextManager.getDebugResources("@");
        debugResources.forEach(docDefHV -> docTypeFlows.put(docDefHV.getDocType(),docDefHV.getFlows()));

        // ???????????????
        List<DocDefFlowV> flows = new ArrayList<>();
        docTypeFlows.values()
                .forEach(list->
                        flows.addAll(list.stream()
                                .filter(item->StringUtils.isBlank(docType) || StringUtils.equalsAny(docType,item.getPreDocType()))
                                .collect(Collectors.toList()))
                );

        return flows;
    }
    /**
     * ??????????????????????????????
     * ?????????????????? ????????????????????? ???????????????????????????
     * @param docType docType
     * @return DocDefHV
     */
    @Override
    @SuppressWarnings("unchecked")
    public DocDefHV getDocDefForRuntime(String docType){

        long start = System.currentTimeMillis();

        if(log.isInfoEnabled())
            log.info("???????????????????????? docType = {} ",
                    docType
            );

        // ????????????????????????debug???????????????????????????debug?????????????????????
        DocDefHV defHV = Optional.ofNullable(
            (DocDefHV)(debugContextManager.getDebugResource(String.format("@%s", docType)))
        ).orElse(
            redisSupport.getIfAbsent(Constants.CACHE_DEF_DOC_TYPES,docType,()-> getLatestActiveVersion(docType))
        );
        Assert.notNull(defHV,String.format("????????????[%s]??????????????????????????????????????????",docType));

        deserializeDefFromContent(defHV);

        runLoopCards(defHV, false,false, (nkCard,item)->{
                item.setDebug(nkCard.isDebug());
                item.setConfig(nkCard.afterGetDef(defHV, item, item.getConfig()));
        });

        defHV.setNextFlows(getDocTypeFlows(docType));
        if(log.isInfoEnabled())
            log.info("?????????????????? docType = {} ?????? {}ms",
                    docType,
                    System.currentTimeMillis() - start
            );

        return defHV;
    }

    /**
     * ??????????????????
     * ?????????????????????????????? ?????????????????????????????????
     * @param docType docType
     * @param version version
     * @return DocDefHV
     */
    @Override
    public DocDefHV getDocDefForEdit(String docType, String version){
        DocDefHV docDefHV = debugContextManager.getDebugResource(String.format("@%s", docType));
        if(docDefHV!=null && StringUtils.equals(docDefHV.getVersion(),version)){
            return deserializeDefFromContent(docDefHV);
        }
        return deserializeDefFromContent(fetchDocDefFromDB(docType, version));
    }

    private DocDefHV fetchDocDefFromDB(String docType, String version){

        DocDefHKey key = new DocDefHKey();
        key.setDocType(docType);
        key.setVersion(version);

        DocDefH docDefH = docDefHMapper.selectByPrimaryKey(key);
        Assert.notNull(docDefH,String.format("????????????[%s]?????????????????????",docType));
        //docDefH.setMarkdown(null);

        DocDefHV def = BeanUtilz.copyFromObject(docDefH,DocDefHV.class);

        // status
        DocDefStateExample stateExample = new DocDefStateExample();
        stateExample.createCriteria()
                .andDocTypeEqualTo(docType)
                .andVersionEqualTo(version);
        stateExample.setOrderByClause("ORDER_BY");
        def.setStatus(BeanUtilz.copyFromList(docDefStateMapper.selectByExample(stateExample), DocDefStateV.class));

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

        // indexRule
        DocDefIndexRuleExample indexRuleExample = new DocDefIndexRuleExample();
        indexRuleExample.createCriteria()
                .andDocTypeEqualTo(docType)
                .andVersionEqualTo(version);
        indexRuleExample.setOrderByClause("ORDER_BY");
        def.setIndexRules(docDefIndexRuleMapper.selectByExample(indexRuleExample));

        // indexCustom
        DocDefIndexCustomExample indexCustomExample = new DocDefIndexCustomExample();
        indexCustomExample.createCriteria()
                .andDocTypeEqualTo(docType)
                .andVersionEqualTo(version);
        indexCustomExample.setOrderByClause("ORDER_BY");
        def.setIndexCustoms(docDefIndexCustomMapper.selectByExample(indexCustomExample));

        // dataSync
        DocDefDataSyncExample dataSyncExample = new DocDefDataSyncExample();
        dataSyncExample.createCriteria()
                .andDocTypeEqualTo(docType)
                .andVersionEqualTo(version);
        dataSyncExample.setOrderByClause("ORDER_BY");
        def.setDataSyncs(docDefDataSyncMapper.selectByExample(dataSyncExample));

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

    @Override
    public DocDefHV deserializeDef(DocDefHV docDefHV) {

        runLoopCards(docDefHV, false,true, (nkCard,item)->{

            log.info("\tdeserializeDef docType = {} cardKey = {}",docDefHV.getDocType(), item.getCardKey());
            item.setPosition(nkCard.getPosition());
            item.setDataComponentName(nkCard.getDataComponentName());
            item.setDefComponentNames(nkCard.getAutoDefComponentNames());
            item.setConfig(nkCard.deserializeDef(item.getConfig()));
            item.setCardContent(null);
        });

        return docDefHV;
    }

    private DocDefHV deserializeDefFromContent(DocDefHV docDefHV) {

        runLoopCards(docDefHV, false,true, (nkCard,item)->{

            log.info("\tdeserializeDef docType = {} cardKey = {}",docDefHV.getDocType(), item.getCardKey());
            item.setPosition(nkCard.getPosition());
            item.setDataComponentName(nkCard.getDataComponentName());
            item.setDefComponentNames(nkCard.getAutoDefComponentNames());
            item.setConfig(nkCard.deserializeDef(item.getCardContent()));
            item.setCardContent(null);
        });

        return docDefHV;
    }

    /**
     * ???????????????????????????
     * @param docDefHV docDefHV
     */
    private void validateDef(DocDefHV docDefHV){

        customObjectManager.getCustomObject(docDefHV.getRefObjectType(), NkCustomObject.class);

        runLoopCards(docDefHV, false,false,(card, docDefIV)->{

        });
    }

    private void runLoopCards(DocDefHV docDefHV, boolean reCalc, boolean ignoreError, Function function){

        List<DocDefIV> cards = docDefHV.getCards()
                .stream()
                .sorted(Comparator.comparing(DocDefI::getCalcOrder))
                .collect(Collectors.toList());

        int times = 1;

        do{
            for(DocDefIV docDefI : cards){
                // ??????????????????????????????
                NkCard nkCard = customObjectManager.getCustomObjectIfExists(docDefI.getBeanName(), NkCard.class);

                if(nkCard==null && !ignoreError){
                    throw new NkDefineException(String.format("???????????????[%s]?????????",docDefI.getBeanName()));
                }

                if(nkCard==null){
                    log.warn("\tdeserializeDef error docType = {} beanName = {} not found",docDefHV.getDocType(), docDefI.getBeanName());
                    return;
                }

                try {
                    function.run(nkCard, docDefI);
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                    if(!ignoreError){
                        throw new NkComponentException(nkCard,e);
                    }
                }
            }
            times ++;

            int finalTimes = times;
            cards = cards.stream()
                    .filter(c->c.getCalcTimes()>= finalTimes)
                    .collect(Collectors.toList());
        }while (reCalc && !cards.isEmpty());

    }
    @Override
    public void runLoopCards(String docId, DocDefHV docDefHV, boolean reCalc, boolean ignoreError, Function function){
        try{
            NkDocEngineThreadLocal.lockDoc(docId);
            this.runLoopCards(docDefHV, reCalc,ignoreError,function);
        }finally {
            NkDocEngineThreadLocal.unlockDoc(docId);
        }
    }

    @Transactional(propagation = Propagation.NEVER)
    @Override
    public Object callDef(Object def, String fromCard, Object options) {

        NkCard card = customObjectManager
                .getCustomObject(fromCard, NkCard.class);

        Object deserializeDef = card.deserializeDef(def);

        @SuppressWarnings("all")
        Object r = card.callDef(deserializeDef, options);

        return r;
    }

    /**
     * ????????????????????????
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

    /**
     * ??????????????????
     * @param docType docType
     * @return DocDefHV
     */
    @Override
    public DocDefHV getDocDefLatestActive(String docType){
        DocDefHV defHV = getLatestActiveVersion(docType);
        if(defHV!=null){
            return deserializeDefFromContent(defHV);
        }
        return null;
    }

    private DocDefHV getLatestActiveVersion(String docType){

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

    @Override
    public void loadExport(JSONArray exports) {

        JSONObject export = new JSONObject();
        export.put("key","docTypes");
        export.put("name","????????????");
        export.put("list",getAllDocTypes()
                                .stream()
                                .map(docDefH -> new NkCustomObjectDesc(docDefH.getDocType(),docDefH.getDocType() + " | " + docDefH.getDocName()))
        );
        exports.add(export);
    }

    @Override
    public void exportConfig(JSONObject config, JSONObject export) {

        if(config.getJSONArray("docTypes")!=null){
            export.put("docTypes",
                    config.getJSONArray("docTypes").stream().map(docType->
                            getDocDefLatestActive((String) docType)
                    ).filter(Objects::nonNull).collect(Collectors.toList())
            );
        }
    }

    @Override
    public void importConfig(JSONObject data) {
        if(data.containsKey("docTypes")){
            data.getJSONArray("docTypes").toJavaList(DocDefHV.class)
                    .forEach(this::doActive);
        }
    }
}
