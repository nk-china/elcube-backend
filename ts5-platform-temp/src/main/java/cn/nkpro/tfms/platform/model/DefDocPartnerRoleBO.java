package cn.nkpro.tfms.platform.model;

import cn.nkpro.tfms.platform.model.po.DefDocPartnerRole;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by bean on 2020/7/14.
 */
public class DefDocPartnerRoleBO extends DefDocPartnerRole {
    @Getter@Setter
    private String partnerRoleDesc;
}
