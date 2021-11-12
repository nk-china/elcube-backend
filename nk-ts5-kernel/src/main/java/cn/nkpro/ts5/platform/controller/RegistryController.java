package cn.nkpro.ts5.platform.controller;

import cn.nkpro.ts5.platform.service.PlatformRegistryService;
import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.platform.gen.PlatformRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 */
@NkNote("32.[DevDef]常量")
@RequestMapping("/sys/constant")
@RestController
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:CONSTANT')")
public class RegistryController {

    @Autowired
    private PlatformRegistryService constantService;

    @NkNote("11、加载常量列表")
    @RequestMapping("/list")
    public List<PlatformRegistry> list(){
        return constantService.getAll();
    }

    @NkNote("12、更新")
    @RequestMapping("/save")
    public void save(@RequestBody List<PlatformRegistry> list){
        constantService.doUpdate(list);
    }
}
