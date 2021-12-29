package cn.nkpro.elcube.components.defaults.util

import cn.nkpro.elcube.docengine.model.DocHV
import cn.nkpro.elcube.platform.gen.UserAccount
import fr.opensagres.xdocreport.document.IXDocReport
import fr.opensagres.xdocreport.template.IContext
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata
import org.springframework.stereotype.Component

import java.util.stream.Collectors

/**
 *@description:word的域 循环数据
 *@Author:YF
 *@date:2021/7/14 18:13
 *@param:
 *@return:
 **/
@Component

class FieldsMetadataPreprocessorPlansImpl implements FieldsMetadataPreprocessor {

    @Override
    void processMeta(IXDocReport report){

        FieldsMetadata metadata = new FieldsMetadata()
        metadata.addFieldAsList("plans.username")
        metadata.addFieldAsList("plans.createdTime")
        report.setFieldsMetadata(metadata)
    }

    @Override
    void processData(DocHV doc, IContext context) {
        List<TestBo> plans = new ArrayList<>()
        TestBo userAccount = new TestBo()
        userAccount.setUsername("11")
        userAccount.setCreatedTime("1233")
        plans.add(userAccount)
        if(plans!=null){
            context.put("plans", plans
                    .stream()
                    .collect(Collectors.toList()))
        }
    }
}
