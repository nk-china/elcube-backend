package cn.nkpro.ts5.dataengine.controller;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.dataengine.service.DashboardService;
import cn.nkpro.ts5.dataengine.service.MeterService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by bean on 2019/12/18.
 */
@NkNote("2.仪表盘控制器")
@RequestMapping("/meter")
@RestController
public class MeterController {

    @Autowired
    private MeterService meterService;

    @NkNote("5、卡片列表")
    @RequestMapping("/card/list")
    public List<JSONObject> cardList(){
        return meterService.getCardList();
    }

    @NkNote("6、加载卡片数据")
    @RequestMapping("/card/get/{meterName}")
    public Object cardDataGet(@PathVariable(value = "meterName") String meterName,@RequestBody Object config){
        return meterService.getCardData(meterName, config);
    }
}
