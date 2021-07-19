package cn.nkpro.tfms.platform.services;

import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.elasticearch.ESRoot;
import cn.nkpro.tfms.platform.exception.TfmsSearchException;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.index.IndexDoc;
import cn.nkpro.tfms.platform.model.po.DefDocType;
import cn.nkpro.tfms.platform.elasticearch.ESPageList;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * Created by bean on 2020/6/10.
 */
public interface TfmsDocService {

    <T extends ESRoot> ESPageList<T> query(Class<T> docType, JSONObject params, QueryBuilder queryBuilder) throws TfmsSearchException;

    List<DefDocType> getDocTypes(String classify);

    List<BizDocBase> getListByRefObject(String refObjectId, String preDocId, EnumDocClassify classify);

    BizDocBase getDocDetail(String docId);

    IndexDoc getDocIndex(BizDocBase doc);

//    @Transactional
//    void doStateUpdate(String docId,String state);

    @Transactional
    void onBpmKilled(String docId);

    Object execCardFunc(String card, String func, String body) throws Throwable;

    void reindex(String docType) throws IOException;

    Object execDocProcessorFunc(String docProcessorName, String json);
}
