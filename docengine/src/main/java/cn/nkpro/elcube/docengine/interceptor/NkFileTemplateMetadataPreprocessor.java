package cn.nkpro.elcube.docengine.interceptor;

import cn.nkpro.elcube.co.NkCustomObject;
import cn.nkpro.elcube.docengine.model.DocHV;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.template.IContext;

/**
  *@description:文件模板导出关于循环数据的处理接口
 **/
public interface NkFileTemplateMetadataPreprocessor extends NkCustomObject {
    void processMeta(IXDocReport report);
    void processData(DocHV doc, IContext context);
}
