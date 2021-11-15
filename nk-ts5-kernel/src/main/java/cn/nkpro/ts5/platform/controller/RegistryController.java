package cn.nkpro.ts5.platform.controller;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.platform.service.PlatformRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 */
@NkNote("32.[DevDef]常量")
@RequestMapping("/platform/registry")
@RestController
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:REGISTRY')")
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
