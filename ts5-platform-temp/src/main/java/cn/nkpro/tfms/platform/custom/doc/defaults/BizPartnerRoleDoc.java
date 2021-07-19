package cn.nkpro.tfms.platform.custom.doc.defaults;


import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.BizPartnerBO;
import cn.nkpro.tfms.platform.model.po.DefPartnerRole;
import lombok.Getter;
import lombok.Setter;


/**
 * Created by bean on 2020/7/30.
 */
public class BizPartnerRoleDoc extends BizDocBase {

    public BizPartnerRoleDoc(){
        super();
    }

    @Setter@Getter
    private BizPartnerBO refObject;
    @Setter@Getter
    private DefPartnerRole definedRefObject;
}
