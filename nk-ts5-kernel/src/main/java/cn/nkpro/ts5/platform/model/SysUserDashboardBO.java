package cn.nkpro.ts5.platform.model;

import cn.nkpro.ts5.platform.gen.UserDashboard;
import cn.nkpro.ts5.platform.gen.UserDashboardRef;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class SysUserDashboardBO {

    @Getter@Setter
    private List<UserDashboardRef> refs;

    @Getter@Setter
    private UserDashboard activeDashboard;

}
