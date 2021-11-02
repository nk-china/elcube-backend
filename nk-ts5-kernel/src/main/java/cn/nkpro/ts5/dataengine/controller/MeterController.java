package cn.nkpro.ts5.dataengine.controller;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.platform.service.DashboardService;
import cn.nkpro.ts5.platform.model.SysUserDashboardBO;
import cn.nkpro.ts5.platform.gen.SysUserDashboard;
import cn.nkpro.ts5.platform.gen.SysUserDashboardRef;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;
import java.util.List;

/**
 * Created by bean on 2019/12/18.
 */
@NkNote("2.仪表盘控制器")
@RequestMapping("/meter")
@RestController
public class MeterController {

    @Autowired
    private DashboardService dashboardService;

    @NkNote("5、卡片列表")
    @RequestMapping("/card/list")
    public List<JSONObject> cardList(){
        return dashboardService.getCardList();
    }

    @NkNote("6、加载卡片数据")
    @RequestMapping("/card/get/{meterName}")
    public Object cardDataGet(@PathVariable(value = "meterName") String meterName,@RequestBody Object config){
        return dashboardService.getCardData(meterName, config);
    }


}
