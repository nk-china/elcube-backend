package cn.nkpro.ts5.co.spel;

import cn.nkpro.ts5.platform.service.PlatformRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("SpELnk")
public class NkDefaultSpEL implements NkSpELInjection {

    @Autowired@SuppressWarnings("all")
    private PlatformRegistryService registryService;

    @SuppressWarnings("unused")
    public Object dict(String key){
        return registryService.getJSON("@DICT",key);
    }
}
