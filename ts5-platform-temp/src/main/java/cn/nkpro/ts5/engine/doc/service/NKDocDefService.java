package cn.nkpro.ts5.engine.doc.service;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.engine.doc.NKCard;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocDefIV;
import cn.nkpro.ts5.model.mb.gen.DocDefH;

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

    Map<String, Object> options(String classify);

    DocDefIV getCardDescribe(String cardHandlerName);

    List<DocDefH> getEntrance(String classify);

    DocDefHV getDocDef(String docType, Integer major);

    DocDefHV getDocDef(String docType, String version, boolean includeComponentMarkdown, boolean ignoreError);

    DocDefHV doEdit(DocDefHV docDefHV);

    DocDefHV doBreach(DocDefHV docDefHV);

    DocDefHV doActive(DocDefHV docDefHV);

    void doDelete(DocDefH docDefHV, boolean force);

    DocDefHV doUpdate(DocDefHV defDocTypeBO, boolean force);

    void runLoopCards(DocDefHV docDefHV, Function function) throws Exception;

    @FunctionalInterface
    interface Function {
        void run(NKCard card, DocDefIV docDefIV) throws Exception;
    }
}