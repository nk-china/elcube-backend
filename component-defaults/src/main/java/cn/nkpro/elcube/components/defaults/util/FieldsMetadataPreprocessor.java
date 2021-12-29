package cn.nkpro.elcube.components.defaults.util;

import cn.nkpro.elcube.co.NkCustomObject;
import cn.nkpro.elcube.docengine.model.DocHV;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.template.IContext;

/**
  *@description:文件模板导出list的处理接口
 **/
public interface FieldsMetadataPreprocessor extends NkCustomObject {
    void processMeta(IXDocReport report);
    void processData(DocHV doc, IContext context);
}
