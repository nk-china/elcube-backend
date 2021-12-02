/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
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
