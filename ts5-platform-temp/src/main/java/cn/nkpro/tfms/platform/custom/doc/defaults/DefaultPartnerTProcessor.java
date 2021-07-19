package cn.nkpro.tfms.platform.custom.doc.defaults;

import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.custom.doc.AbstractDocProcessor;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.services.TfmsDefPartnerRoleService;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("nkDefaultPartnerTransactionProcessor")
public class DefaultPartnerTProcessor extends AbstractDocProcessor {

    @Autowired
    private TfmsDefPartnerRoleService defPartnerRoleService;

    @Override
    public String getDocHeaderComponentName() {
        return "nk-page-partner-header-transaction";
    }

    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.PARTNER_T;
    }

    @Override
    public Class<? extends BizDocBase> dataType() {
        return BizPartnerTranDoc.class;
    }

    @Override
    public BizDocBase detailAfterComponent(BizDocBase doc) {

        BizPartnerTranDoc tranDoc = (BizPartnerTranDoc) doc;
        Assert.notNull(tranDoc.getPreDocId(),"前序单据不存在");

        BizPartnerRoleDoc partnerDoc = (BizPartnerRoleDoc) docService.getDocDetail(tranDoc.getRefObjectId());
        tranDoc.setDefinedRefObject(defPartnerRoleService.getPartnerRoleRuntimeDefined(partnerDoc,tranDoc));
        tranDoc.setRefObject(partnerDoc.getRefObject());
        tranDoc.setPartnerName(partnerDoc.getPartnerName());

        return tranDoc;
    }

    @Override
    public BizDocBase createBeforeComponent(BizDocBase doc) {
        BizPartnerTranDoc tranDoc = (BizPartnerTranDoc) super.createBeforeComponent(doc);

        BizPartnerRoleDoc partnerDoc = (BizPartnerRoleDoc) docService.getDocDetail(doc.getRefObjectId());
        tranDoc.setDefinedRefObject(defPartnerRoleService.getPartnerRoleRuntimeDefined(partnerDoc,tranDoc));
        tranDoc.setRefObject(partnerDoc.getRefObject());
        tranDoc.setPartnerName(partnerDoc.getPartnerName());

        return tranDoc;
    }
}
