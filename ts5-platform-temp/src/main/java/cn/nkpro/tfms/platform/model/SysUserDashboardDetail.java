package cn.nkpro.tfms.platform.model;

import cn.nkpro.tfms.platform.model.po.SysUserDashboard;
import cn.nkpro.tfms.platform.model.po.SysUserDashboardRef;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class SysUserDashboardDetail {

    @Getter@Setter
    private List<SysUserDashboardRef> refs;

    @Getter@Setter
    private SysUserDashboard activeDashboard;

}
