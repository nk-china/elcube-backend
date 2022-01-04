package cn.nkpro.elcube.components.financial.services

import cn.nkpro.elcube.co.easy.EasyCollection
import cn.nkpro.elcube.components.financial.cards.NkCardPaymentSchedule
import cn.nkpro.elcube.docengine.interceptor.abstracts.NkAbstractFileTemplateMetadataPreprocessor
import cn.nkpro.elcube.docengine.model.DocHV
import fr.opensagres.xdocreport.document.IXDocReport
import fr.opensagres.xdocreport.template.IContext
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata
import org.springframework.stereotype.Component

import java.util.stream.Collectors

/**
 *@description:word的域 还款计划的数据处理
 **/
@Component("NkFileTemplateMetadataPlansPreprocessorImpl")
class NkFileTemplateMetadataPlansPreprocessorImpl extends NkAbstractFileTemplateMetadataPreprocessor{

    @Override
    void processMeta(IXDocReport report){

        FieldsMetadata metadata = new FieldsMetadata()
        metadata.addFieldAsList("plans.period")
        metadata.addFieldAsList("plans.expireDate")
        metadata.addFieldAsList("plans.pay")
        metadata.addFieldAsList("plans.principal")
        metadata.addFieldAsList("plans.interest")
        metadata.addFieldAsList("plans.residual")
        report.setFieldsMetadata(metadata)
    }

    @Override
    void processData(DocHV doc, IContext context) {
        List<NkCardPaymentSchedule.PaymentI> paymentIList = new ArrayList<>()
        EasyCollection collection = doc.fetchList("payment")
        collection.forEach({ l ->
            paymentIList.add(l.target())
        })
        if(paymentIList!=null){
            context.put("plans", paymentIList
                    .stream()
                    .collect(Collectors.toList()))
        }
    }
}
