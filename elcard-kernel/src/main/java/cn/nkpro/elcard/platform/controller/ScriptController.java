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

import cn.nkpro.elcard.basic.NkProperties;
import cn.nkpro.elcard.basic.PageList;
import cn.nkpro.elcard.co.*;
import cn.nkpro.elcard.platform.gen.PlatformScript;
import cn.nkpro.elcard.platform.service.NkScriptManager;
import cn.nkpro.elcard.annotation.NkNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Created by bean on 2020/7/17.
 */
@NkNote("34.[DevDef]脚本配置")
@RestController
@RequestMapping("/def/script")
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:COMPONENT')")
public class ScriptController {


    @Autowired@SuppressWarnings("all")
    private NkCustomObjectManager customObjectManager;
    @Autowired@SuppressWarnings("all")
    private NkScriptManager scriptService;
    @Autowired@SuppressWarnings("all")
    private NkProperties properties;

    @NkNote("1、获取脚本列表")
    @RequestMapping("/page")
    public PageList<PlatformScript> page(
            @NkNote("查询关键字")
            @RequestParam(value = "keyword",    required = false)           String keyword,
            @NkNote("分类")
            @RequestParam(value = "type",       required = false)           String type,
            @NkNote("版本")
            @RequestParam(value = "version",    required = false)           String version,
            @NkNote("状态")
            @RequestParam(value = "state",      required = false)           String state,
            @NkNote("起始条目")
            @RequestParam(value = "from",       defaultValue = "0")         Integer from,
            @NkNote("条目数")
            @RequestParam(value = "rows",       defaultValue = "10")        Integer rows,
            @NkNote("排序字段")
            @RequestParam(value = "orderField", defaultValue = "")          String orderField,
            @NkNote("排序方式")
            @RequestParam(value = "order",      defaultValue = "")          String order){
        return scriptService.getPage(keyword,type,version,state,from,rows,orderField,order);
    }

    @NkNote("2、通过脚本ID获取脚本")
    @RequestMapping("/detail/{script}/{version}")
    public PlatformScript detail(@PathVariable("script") String scriptName, @PathVariable String version){
        return scriptService.getScript(scriptName,version);
    }

    @NkNote("4.复制")
    @RequestMapping("/breach")
    public PlatformScript breach(
            @NkNote("脚本对象")@RequestBody NkScriptV script){
        return scriptService.doBreach(script);
    }

    @NkNote("5.更新")
    @RequestMapping("/update")
    public PlatformScript update(
            @NkNote("脚本对象")@RequestBody NkScriptV script){
        return scriptService.doUpdate(script, false);
    }

    @NkNote("6.激活")
    @RequestMapping("/active")
    public PlatformScript active(
            @NkNote("脚本对象")@RequestBody NkScriptV script,@RequestParam(value = "force",defaultValue = "false",required = false) Boolean force){
        return scriptService.doActive(script, force);
    }

    @NkNote("7.删除")
    @RequestMapping("/delete")
    public void delete(
            @NkNote("脚本对象")@RequestBody NkScriptV script){
        scriptService.doDelete(script);
    }

    @NkNote("8.调试")
    @RequestMapping("/debug")
    public PlatformScript debug(
            @NkNote("脚本对象")@RequestBody NkScriptV script, @RequestParam(value="run")boolean run){
        return scriptService.doRun(script,run);
    }

    @NkNote("10.是否禁用在线编辑")
    @RequestMapping("/online/editing")
    public boolean isComponentDisableOnlineEditing(){
        return properties.isComponentDisableOnlineEditing();
    }

}
