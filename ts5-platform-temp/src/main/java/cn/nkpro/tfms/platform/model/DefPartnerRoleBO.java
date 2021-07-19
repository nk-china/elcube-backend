package cn.nkpro.tfms.platform.model;

import cn.nkpro.tfms.platform.model.po.DefPartnerRole;
import lombok.Data;

import java.util.List;

/**
 * Created by bean on 2020/7/15.
 */
@Data
public class DefPartnerRoleBO extends DefPartnerRole {
    private List<DefPartnerRoleDocLI> docs;
}
