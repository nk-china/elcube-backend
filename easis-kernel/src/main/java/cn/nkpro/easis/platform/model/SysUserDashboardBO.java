package cn.nkpro.easis.platform.model;

import cn.nkpro.easis.platform.gen.UserDashboard;
import cn.nkpro.easis.platform.gen.UserDashboardRef;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class SysUserDashboardBO {

    @Getter@Setter
    private List<UserDashboardRef> refs;

    @Getter@Setter
    private UserDashboard activeDashboard;

}
