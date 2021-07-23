package cn.nkpro.ts5.engine.doc.service;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocDefIV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.model.mb.gen.DocDefH;
import org.springframework.transaction.annotation.Transactional;

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

    DocDefHV doEdit(DocDefHV docDefHV);

    DocDefHV doBreach(DocDefHV docDefHV);

    DocDefHV doActive(DocDefHV docDefHV);

    void doDelete(DocDefH docDefHV, boolean force);

    @Transactional
    DocDefHV doUpdate(DocDefHV defDocTypeBO, boolean force);

    List<DocDefH> getEntrance(String classify);

    DocDefHV getDocDefined(String docType);

    DocDefHV getDocDefined(String docType,String version, boolean includeComponentMarkdown, boolean ignoreError);

    DocDefHV getDocDefinedRuntime(String docType, DocHV doc);
}