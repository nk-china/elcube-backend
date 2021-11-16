package cn.nkpro.ts5.platform.controller;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.platform.gen.UserDashboard;
import cn.nkpro.ts5.platform.gen.UserDashboardRef;
import cn.nkpro.ts5.platform.model.SysUserDashboardBO;
import cn.nkpro.ts5.platform.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by bean on 2019/12/18.
 */
@NkNote("5.仪表盘控制器")
@RequestMapping("/dashboard")
@RestController
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @NkNote("1、加载仪表盘")
    @RequestMapping("/load")
    public SysUserDashboardBO load(String dashboardId){
        return dashboardService.loadUserDashboards(dashboardId);
    }

    @NkNote("2、保存仪表盘列表")
    @ResponseBody
    @RequestMapping("/updateRefs")
    public void update(@RequestBody List<UserDashboardRef> refs){
        dashboardService.doUpdateRefs(refs);
    }

    @NkNote("3、保存仪表盘布局")
    @ResponseBody
    @RequestMapping("/update")
    public void update(@RequestBody UserDashboard dashboard){
        dashboardService.doUpdate(dashboard);
    }

    @NkNote("4、删除仪表盘")
    @ResponseBody
    @RequestMapping("/del")
    public void del(String dashboardId){
        dashboardService.doDel(dashboardId);
    }
}
