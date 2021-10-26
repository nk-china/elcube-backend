package cn.nkpro.ts5.platform.model;

import cn.nkpro.ts5.platform.gen.SysUserDashboard;
import cn.nkpro.ts5.platform.gen.SysUserDashboardRef;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class SysUserDashboardBO {

    @Getter@Setter
    private List<SysUserDashboardRef> refs;

    @Getter@Setter
    private SysUserDashboard activeDashboard;

}
