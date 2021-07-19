package cn.nkpro.tfms.platform.services;

import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.DefPartnerRoleBO;
import cn.nkpro.tfms.platform.model.po.DefPartnerRole;
import cn.nkpro.tfms.platform.model.util.PageList;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2020/7/15.
 */
public interface TfmsDefPartnerRoleService {

    PageList<DefPartnerRole> getPage(String partnerRole, String keyword, Integer from, Integer rows, String orderField, String order);

    DefPartnerRole getPartnerRoleRuntimeDefined(BizDocBase partner, BizDocBase preDoc);

    DefPartnerRole getPartnerRoleDefined(String partnerRole);

    Map<String,Object> options();

    @Transactional
    void doUpdate(DefPartnerRoleBO defPartnerRole, Boolean create,boolean force);

    List<DefPartnerRole> getAllRoles(boolean filterByPerm);
}
