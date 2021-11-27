package cn.nkpro.easis.docengine.service;

import cn.nkpro.easis.basic.PageList;
import cn.nkpro.easis.docengine.NkCard;
import cn.nkpro.easis.docengine.gen.DocDefH;
import cn.nkpro.easis.docengine.model.DocDefFlowV;
import cn.nkpro.easis.docengine.model.DocDefHV;
import cn.nkpro.easis.docengine.model.DocDefIV;

import java.util.List;
import java.util.Map;

public interface NkDocDefService {

    PageList<DocDefH> getPage(String docClassify,
                              String docType,
                              String state,
                              String keyword,
                              int from,
                              int rows,
                              String orderField,
                              String order);

    List<DocDefH> getAllDocTypes();

    List<DocDefH> getList(String docType, int page);

    Map<String, Object> options(String classify);

    DocDefIV getCardDescribe(String cardHandlerName);

    List<DocDefFlowV> getEntrance(String classify);

    DocDefHV doRun(DocDefHV docDefHV, boolean run);

    DocDefHV getDocDefForRuntime(String docType);

    DocDefHV getDocDefForEdit(String docType, String version);

    DocDefHV doBreach(DocDefHV docDefHV);

    DocDefHV doActive(DocDefHV docDefHV);

    void doDelete(DocDefH docDefHV, boolean force);

    DocDefHV doUpdate(DocDefHV defDocTypeBO, boolean force);

    DocDefHV deserializeDef(DocDefHV docDefHV);

    Object callDef(Object def, String fromCard, Object options);

    void runLoopCards(String docId, DocDefHV docDefHV, boolean ignoreError, Function function);

    DocDefHV getDocDefLatestActive(String docType);

    @FunctionalInterface
    interface Function {
        void run(NkCard card, DocDefIV docDefIV) throws Exception;
    }
}