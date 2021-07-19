package cn.nkpro.tfms.platform.services;

import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.model.BizDocBase;

import java.util.List;

/**
 *
 * 单据引擎，用来开放给组件的服务
 * Created by bean on 2021/1/8
 */
public interface TfmsDocEngine {

    BizDocBase toCreate(String refObjectId, String preDocId, String docType);

    BizDocBase calculate(BizDocBase doc);

    BizDocBase doUpdate(BizDocBase doc, String source);

    BizDocBase getDocDefined(String docId);

    BizDocBase getDocDetail(String docId);

    List<BizDocBase> getListByRefObject(String refObjectId, String preDocId, EnumDocClassify classify);

    void index(BizDocBase doc);
}
