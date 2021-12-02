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

import cn.nkpro.easis.platform.service.PlatformRegistryService;
import cn.nkpro.easis.annotation.NkNote;
import cn.nkpro.easis.platform.gen.PlatformRegistry;
import cn.nkpro.easis.platform.service.impl.PlatformRegistryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 */
@NkNote("32.[DevDef]常量")
@RequestMapping("/platform/registry/def")
@RestController
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:REGISTRY')")
public class RegistryDefController {

    @Autowired
    private PlatformRegistryService constantService;

    @NkNote("11、加载常量列表")
    @RequestMapping("/tree")
    public List<PlatformRegistryServiceImpl.TreeNode> tree(){
        return constantService.getTree();
    }

    @NkNote("11、加载常量列表")
    @RequestMapping(value = "/value/{key}",method = RequestMethod.GET)
    public PlatformRegistry value(@PathVariable("key") String key){
        return constantService.getValue(key);
    }

    @NkNote("11、加载常量列表")
    @RequestMapping(value = "/value", method = RequestMethod.POST)
    public void post(@RequestBody PlatformRegistry registry){
        constantService.updateValue(registry);
    }

    @NkNote("11、加载常量列表")
    @RequestMapping(value = "/value/delete", method = RequestMethod.POST)
    public void delete(@RequestBody PlatformRegistry registry){
        constantService.deleteValue(registry);
    }

    @NkNote("12、更新")
    @RequestMapping("/save")
    public void save(@RequestBody List<PlatformRegistry> list){
        constantService.doUpdate(list);
    }
}
