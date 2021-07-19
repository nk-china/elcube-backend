package cn.nkpro.tfms.platform.model;

import cn.nkpro.tfms.platform.model.po.BizPartner;
import cn.nkpro.tfms.platform.model.po.DefPartnerRole;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * Created by bean on 2020/7/15.
 */
public class BizPartnerBO extends BizPartner {

    @Getter@Setter
    private List<BizDocBase> roles = Collections.emptyList();

    @Getter@Setter
    private List<DefPartnerRole> defRoles;
}
