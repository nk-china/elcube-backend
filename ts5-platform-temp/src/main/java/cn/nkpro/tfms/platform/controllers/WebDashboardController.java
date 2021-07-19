package cn.nkpro.tfms.platform.controllers;

import cn.nkpro.tfms.platform.model.SysUserDashboardDetail;
import cn.nkpro.tfms.platform.model.po.SysUserDashboard;
import cn.nkpro.tfms.platform.model.po.SysUserDashboardRef;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.tfms.platform.services.impl.TfmsDashboardService;
import com.alibaba.fastjson.JSONObject;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by bean on 2019/12/18.
 */
@WsDocNote("2.仪表盘控制器")
@RequestMapping("/dashboard")
@Controller
public class WebDashboardController {

    @Autowired
    private TfmsDashboardService dashboardService;

    @WsDocNote("1、加载仪表盘")
    @CompressResponse
    @RequestMapping("/load")
    public SysUserDashboardDetail load(String dashboardId){
        return dashboardService.loadUserDashboards(dashboardId);
    }

    @WsDocNote("2、保存仪表盘列表")
    @ResponseBody
    @CompressResponse
    @RequestMapping("/updateRefs")
    public void update(@RequestBody List<SysUserDashboardRef> refs){
        dashboardService.doUpdateRefs(refs);
    }

    @WsDocNote("3、保存仪表盘布局")
    @ResponseBody
    @CompressResponse
    @RequestMapping("/update")
    public void update(@RequestBody SysUserDashboard dashboard){
        dashboardService.doUpdate(dashboard);
    }

    @WsDocNote("4、删除仪表盘")
    @ResponseBody
    @CompressResponse
    @RequestMapping("/del")
    public void del(String dashboardId){
        dashboardService.doDel(dashboardId);
    }

    @WsDocNote("5、卡片列表")
    @CompressResponse
    @RequestMapping("/card/list")
    public List<JSONObject> cardList(){
        return dashboardService.getCardList();
    }


}
