package cn.nkpro.easis.platform.controller;

import cn.nkpro.easis.annotation.NkNote;
import cn.nkpro.easis.platform.service.MeterService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by bean on 2019/12/18.
 */
@NkNote("6.仪表组件控制器")
@RequestMapping("/meter")
@RestController
public class MeterController {

    @Autowired
    private MeterService meterService;

    @NkNote("1.卡片列表")
    @RequestMapping("/card/list")
    public List<JSONObject> cardList(){
        return meterService.getCardList();
    }

    @NkNote("1.卡片列表")
    @RequestMapping("/card/defs")
    public List<JSONObject> cardListDefs(){
        return meterService.getCardDefs();
    }

    @NkNote("2.加载卡片数据")
    @RequestMapping("/card/get/{meterName}")
    public Object cardDataGet(@PathVariable(value = "meterName") String meterName,@RequestBody Object config){
        return meterService.getCardData(meterName, config);
    }
}
