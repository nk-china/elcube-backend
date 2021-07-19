package cn.nkpro.tfms.platform.services;

import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import cn.nkpro.tfms.platform.model.po.BizDoc;
import cn.nkpro.tfms.platform.model.po.DefDocComponentKey;
import cn.nkpro.tfms.platform.model.po.DefDocType;
import cn.nkpro.tfms.platform.model.util.PageList;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2020/7/13.
 */
public interface TfmsDefDocTypeService {

    PageList<DefDocType> getPage(String docClassify, String docType, String keyword, int from, int rows, String orderField, String order);

    List<DefDocType> getDocTypes(EnumDocClassify classify);

    List<Map<String, String>> classifys();

    Map<String, Object> options(EnumDocClassify classify);

    @Transactional
    void doUpdate(DefDocTypeBO defDocTypeBO, boolean create, boolean force);

    String getDocComponentDefMarkdown(DefDocComponentKey key);

    DefDocTypeBO getDocDefined(String docType);

    DefDocTypeBO getDocDefined(String docType,Integer version, boolean includeComponentDef, boolean includeComponentMarkdown, boolean ignoreError);

    DefDocTypeBO getDocDefinedRuntime(String docType, BizDoc doc);
}
