/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.platform.controller;

import cn.nkpro.elcube.annotation.NkNote;
import cn.nkpro.elcube.platform.model.MobileOfficeAccProperties;
import cn.nkpro.elcube.platform.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2019/12/18.
 */
@SuppressWarnings("all")
@NkNote("1.微信一键登录小程序")
@RequestMapping("/weChat")
@RestController
public class WeChatController {


    @Autowired
    private NkMobileService nkMobileService;
    @Autowired
    private MobileOfficeAccProperties mobileOfficeAccProperties;
    @Autowired
    private NkDocOperationService nkDocOperationService;

    @NkNote("1.微信小程序根据code获取手机号")
    @RequestMapping("/phoneInfo")
    public Map<String,Object> getPhoneInfo(@RequestParam("code") String code){
        return nkMobileService.getPhoneInfo(code);
    }

    @NkNote("2.微信手机号一键登录小程序")
    @RequestMapping("/login")
    public Map<String,Object> login(@RequestParam("phone") String phone){
        return nkMobileService.weChatLogin(phone);
    }


    @NkNote("3.根据手机号索引查找单据")
    @RequestMapping("/queryDocId")
    public Object queryDocId(@RequestParam("phone") String phone) {
        Map<String,Object> map = new HashMap<>();
        map.put("phone_keyword",phone);
        List<Object> list = nkDocOperationService.getDocByDocContent(map,"TP02");
        if(CollectionUtils.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }
}
