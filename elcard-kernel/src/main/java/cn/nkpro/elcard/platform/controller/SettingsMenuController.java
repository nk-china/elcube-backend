/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.platform.controller;

import cn.nkpro.elcard.platform.service.WebMenuService;
import cn.nkpro.elcard.platform.model.WebMenuBO;
import cn.nkpro.elcard.annotation.NkNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by bean on 2019/12/18.
 */
@NkNote("11.菜单设置")
@RequestMapping("/settings/menu")
@RestController
@PreAuthorize("hasAnyAuthority('*:*','SETTINGS:*','SETTINGS:MENU')")
public class SettingsMenuController {

    @Autowired@SuppressWarnings("all")
    private WebMenuService webappMenuService;

    @NkNote("1.加载Web主菜单")
    @RequestMapping("/menus")
    public List<WebMenuBO> menus(){
        return webappMenuService.getMenus(false);
    }


    @NkNote("2.更新菜单")
    @RequestMapping("/save")
    public void save(@RequestBody List<WebMenuBO> menus){
        webappMenuService.doUpdate(menus);
    }
}
