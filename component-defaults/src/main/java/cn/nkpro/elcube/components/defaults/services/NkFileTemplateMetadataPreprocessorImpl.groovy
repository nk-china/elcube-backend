package cn.nkpro.elcube.components.defaults.services

import cn.nkpro.elcube.docengine.interceptor.abstracts.NkAbstractFileTemplateMetadataPreprocessor
import cn.nkpro.elcube.docengine.model.DocHV
import fr.opensagres.xdocreport.document.IXDocReport
import fr.opensagres.xdocreport.template.IContext
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata
import org.springframework.stereotype.Component

import java.util.stream.Collectors

/**
 *@description:word的域 循环数据的处理
 **/
@Component("NkFileTemplateMetadataPreprocessorImpl")
class NkFileTemplateMetadataPreprocessorImpl extends NkAbstractFileTemplateMetadataPreprocessor{

    @Override
     void processMeta(IXDocReport report){

        FieldsMetadata metadata = new FieldsMetadata()
        metadata.addFieldAsList("certList.username")
        metadata.addFieldAsList("certList.createdTime")
        report.setFieldsMetadata(metadata)
    }

    @Override
    void processData(DocHV doc, IContext context) {
        List<TestBo> certList = new ArrayList<>()
        TestBo userAccount = new TestBo()
        userAccount.setUsername("11")
        userAccount.setCreatedTime("1233")
        certList.add(userAccount)
        if(certList!=null){
            context.put("certList", certList
                    .stream()
                    .collect(Collectors.toList()))
        }

    }
    static class TestBo{
        String username
        String createdTime
    }
}
