package cn.nkpro.ts5.engine.doc.service;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.engine.doc.NKCard;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocDefIV;
import cn.nkpro.ts5.orm.mb.gen.DocDefH;

import java.util.List;
import java.util.Map;

public interface NKDocDefService {

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

    List<DocDefH> getEntrance(String classify);

    DocDefHV doRun(DocDefHV docDefHV);

    DocDefHV getDocDefForRuntime(String docType);

    DocDefHV getDocDefForEdit(String docType, String version);

    DocDefHV doBreach(DocDefHV docDefHV);

    DocDefHV doActive(DocDefHV docDefHV);

    void doDelete(DocDefH docDefHV, boolean force);

    DocDefHV doUpdate(DocDefHV defDocTypeBO, boolean force);

    void runLoopCards(DocDefHV docDefHV, boolean ignoreError, Function function) throws Exception;

    @FunctionalInterface
    interface Function {
        void run(NKCard card, DocDefIV docDefIV) throws Exception;
    }
}