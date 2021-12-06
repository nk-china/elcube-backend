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
import cn.nkpro.elcube.platform.service.PlatformRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 */
@NkNote("32.[DevDef]常量")
@RequestMapping("/platform/registry")
@RestController
@PreAuthorize("authenticated")
public class RegistryController {

    @Autowired
    private PlatformRegistryService constantService;

    @RequestMapping(value = "/value/json/{type}/{key}", method = RequestMethod.GET)
    public Object get(@PathVariable String type, @PathVariable String key){
        return constantService.getJSON(type,key);
    }

    @RequestMapping(value = "/value/list/{type}/{keyPrefix}", method = RequestMethod.GET)
    public Object getList(@PathVariable String type, @PathVariable String keyPrefix){
        return constantService.getList(type,keyPrefix);
    }
}
