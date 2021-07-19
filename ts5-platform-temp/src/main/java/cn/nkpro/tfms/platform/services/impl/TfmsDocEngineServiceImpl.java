package cn.nkpro.tfms.platform.services.impl;

import cn.nkpro.tfms.platform.basis.Constants;
import cn.nkpro.tfms.platform.basis.TfmsCustomObjectManager;
import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.custom.HttpFunc;
import cn.nkpro.tfms.platform.custom.TfmsCardComponent;
import cn.nkpro.tfms.platform.custom.doc.TfmsDocProcessor;
import cn.nkpro.tfms.platform.elasticearch.ESPageList;
import cn.nkpro.tfms.platform.elasticearch.ESRoot;
import cn.nkpro.tfms.platform.elasticearch.SearchEngine;
import cn.nkpro.ts5.exception.TfmsIllegalContentException;
import cn.nkpro.tfms.platform.exception.TfmsSearchException;
import cn.nkpro.tfms.platform.mappers.gen.BizDocMapper;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import cn.nkpro.tfms.platform.model.index.IndexDoc;
import cn.nkpro.tfms.platform.model.index.IndexPartner;
import cn.nkpro.tfms.platform.model.po.BizDoc;
import cn.nkpro.tfms.platform.model.po.BizDocExample;
import cn.nkpro.tfms.platform.model.po.DefDocType;
import cn.nkpro.tfms.platform.services.*;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.ts5.supports.SequenceSupport;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * 单据核心逻辑
 * Created by bean on 2020/6/10.
 */
@Slf4j
@Service
public class TfmsDocEngineServiceImpl extends TfmsDocEngineBaseImpl implements TfmsDocEngine, TfmsDocService {


    @SuppressWarnings("all")@Autowired
    private BizDocMapper bizDocMapper;
    @SuppressWarnings("all")@Autowired
    private SearchEngine searchEngine;
    @SuppressWarnings("all")@Autowired
    private SequenceSupport sequenceUtils;
    @SuppressWarnings("all")@Autowired
    private RedisSupport<BizDocBase> redisSupport;
    @SuppressWarnings("all")@Autowired
    private RedisSupport<List<BizDocBase>> redisSupportList;
    @SuppressWarnings("all")@Autowired
    private TfmsCustomObjectManager customObjectManager;
    @SuppressWarnings("all")@Autowired
    private TfmsPermService permService;
    @SuppressWarnings("all")@Autowired
    private TfmsIndexService indexService;
    @SuppressWarnings("all")@Autowired
    private TfmsDefDocTypeService defDocTypeService;

    /**
     * 查询单据列表数据
     * @param docType       索引类型Type
     * @param params        查询条件
     * @param queryBuilder  预编译的前置查询条件
     * @param <T>           索引类型
     * @return  返回索引列表
     * @throws TfmsSearchException 搜索引擎错误
     */
    @Override
    public <T extends ESRoot> ESPageList<T> query(Class<T> docType, JSONObject params, QueryBuilder queryBuilder) throws TfmsSearchException {
        return indexService.queryList(docType, queryBuilder,params);
    }

    /**
     * 获取单据分类下的单据类型列表
     * @param classify 单据分类
     * @return List
     */
    @Override
    public List<DefDocType> getDocTypes(String classify){
        List<DefDocType> list = defDocTypeService.getDocTypes(EnumDocClassify.valueOf(classify))
                .stream()
                .filter(defDocType -> permService.hasDocPerm(TfmsPermService.MODE_WRITE,defDocType.getDocType()))
                .filter(defDocType ->
                        customObjectManager
                                .getCustomObject(defDocType.getRefObjectType(),TfmsDocProcessor.class)
                                .standalone()
                ).collect(Collectors.toList());

        // todo 增加权限过滤
        list.removeIf(defDocType ->
            !customObjectManager
                    .getCustomObject(defDocType.getRefObjectType(),TfmsDocProcessor.class)
                    .standalone()
        );
        return list;
    }

    /**
     * 查询指定单据的后续单据列表
     * @param refObjectId   nullable 根单据ID
     * @param preDocId      nullable 前序单据ID
     * @param classify      nullable 单据分类
     * @return 后续单据列表
     */
    @Override
    public List<BizDocBase> getListByRefObject(String refObjectId, String preDocId, EnumDocClassify classify){
        return redisSupportList.getIfAbsent(Constants.CACHE_DOC_HISTORY,refObjectId,()->{

            BizDocExample example = new BizDocExample();
            example.createCriteria()
                    .andRefObjectIdEqualTo(refObjectId);
            example.setOrderByClause("CREATED_TIME");

            return BeanUtilz.copyFromList(
                    bizDocMapper.selectByExample(example),
                    BizDocBase.class
            );
        }).stream()
            .filter(doc->StringUtils.isBlank(preDocId) || StringUtils.equals(doc.getPreDocId(),preDocId))
            .filter(doc->classify==null || StringUtils.equals(doc.getClassify(),classify.name()))
            .collect(Collectors.toList());
    }

    @Override
    public BizDocBase toCreate(String refObjectId, String preDocId, String docType) {

        log.debug("===> 准备更新单据");
        // 单据配置数据，并过滤权限
        DefDocTypeBO def = defDocTypeService.getDocDefinedRuntime(docType, null);

        // 创建单据
        BizDocBase doc = customObjectManager
                .getCustomObject(def.getRefObjectType(), TfmsDocProcessor.class)
                .create(def, refObjectId, preDocId);
        doc.setWriteable(true);

        return doc;
    }

    /**
     * 获取单据详情
     * @param docId 单据ID
     * @return 单据
     */
    @Override
    public BizDocBase getDocDetail(String docId) {
        return execGetDocDetail(docId,true,true, false);
    }

    @Override
    public BizDocBase getDocDefined(String docId) {
        return execGetDocDetail(docId,true,false, false);
    }


    @Override
    public BizDocBase calculate(BizDocBase doc){
        TfmsDocProcessor docProcessor = customObjectManager
                .getCustomObject(doc.getDefinedDoc().getRefObjectType(), TfmsDocProcessor.class);
        return docProcessor.calculate(StringUtils.EMPTY,StringUtils.EMPTY,doc);
    }

    @Override
    @Transactional
    public BizDocBase doUpdate(BizDocBase doc,String source) {
        return execUpdate(
                doc,
                execGetDocDetail(doc.getDocId(),false,true, false),
                doc.getDefinedDoc(),
                source,
                true
        );
    }

    @Override
    @Transactional
    public void onBpmKilled(String docId) {

        BizDocBase doc = getDocDetail(docId);
        String oldDocState = doc.getDocState();

        if(doc.getDefinedDoc().getBpm()!=null){

            doc.setDocState(doc.getDefinedDoc().getBpm().getRollbackTo());
            doc.setUpdatedTime(DateTimeUtilz.nowSeconds());
            bizDocMapper.updateByPrimaryKeySelective(doc);

            customObjectManager
                    .getCustomObject(doc.getDefinedDoc().getRefObjectType(), TfmsDocProcessor.class)
                    .stateChanged(doc, oldDocState);

            index(doc);
            redisSupport.delete(String.format("%s%s",Constants.CACHE_DOC , docId));
        }
    }

    @Override
    public Object execDocProcessorFunc(String docProcessorName, String json) {
        /*
         * 通过单据类型 获取默认单据配置，注意这里没有指定版本号
         */
        log.debug("docProcessorName = "+docProcessorName);
        return customObjectManager
                .getCustomObject(docProcessorName, TfmsDocProcessor.class)
                .exec(json);
    }

    @Override
    public Object execCardFunc(String card, String func, String body) throws Throwable {

        TfmsCardComponent tfmsCard = customObjectManager.getCustomObject(card, TfmsCardComponent.class);

        Method method = Arrays.stream(tfmsCard.getClass().getDeclaredMethods())
                .filter(m -> m.getName().equals(func))
                .findAny()
                .orElseThrow(()->new TfmsIllegalContentException(String.format("Func %s 没有找到",func)));

        Assert.isTrue(method.getAnnotation(HttpFunc.class)!=null && (method.getParameterTypes().length<=1),
                String.format("Func %s 不支持远程调用",func));

        Object[] params = method.getParameterTypes().length==1?new Object[]{
                JSON.parseObject(body,method.getParameterTypes()[0])}: new Object[0];
        try{
            method.setAccessible(true);
            return method.invoke(tfmsCard, params);
        } catch (JSONException e){
            throw new IllegalArgumentException("数据转换错误:"+e.getMessage());
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    @Override
    public IndexDoc getDocIndex(BizDocBase doc){
        return customObjectManager
                .getCustomObject(doc.getDefinedDoc().getRefObjectType(), TfmsDocProcessor.class)
                .buildIndex(doc);
    }

    @Override
    public void reindex(String docType) throws IOException {

        BizDocExample example = new BizDocExample();
        BizDocExample.Criteria criteria = example.createCriteria();
        example.setOrderByClause("UPDATED_TIME DESC");

        if(StringUtils.isBlank(docType)) {
            searchEngine.deleteIndices(IndexPartner.class);
            searchEngine.createIndices(IndexPartner.class);

            searchEngine.deleteIndices(IndexDoc.class);
            searchEngine.createIndices(IndexDoc.class);
        }else{
            criteria.andDocTypeEqualTo(docType);
        }
        for (BizDoc bizDoc : bizDocMapper.selectByExample(example)){
            try {
                index(execGetDocDetail(bizDoc.getDocId(),true,true, true));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
