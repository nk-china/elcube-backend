package cn.nkpro.tfms.platform.services;

import cn.nkpro.tfms.platform.model.BizDocBase;
import org.springframework.transaction.annotation.Transactional;

public interface TfmsDocEngineWithPerm {

    BizDocBase toCreateForController(String refObjectId, String preDocId, String docType);

    BizDocBase getDetailHasDocPermForController(String docId);

    BizDocBase calculateForController(String docType, String component, String calculate, String data);

    Object callForController(String docType, String component, String event, String data);

    boolean preUpdateForController(String docType, String docId);

    @Transactional
    BizDocBase doUpdateForController(String docType, String data, String source);
}
