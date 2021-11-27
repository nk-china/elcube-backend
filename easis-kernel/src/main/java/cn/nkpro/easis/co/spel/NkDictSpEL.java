package cn.nkpro.easis.co.spel;

import cn.nkpro.easis.platform.service.PlatformRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("SpELdict")
public class NkDictSpEL implements NkSpELInjection {

    @Autowired@SuppressWarnings("all")
    private PlatformRegistryService registryService;

    @SuppressWarnings("unused")
    public Object json(String key){
        return registryService.getJSON("@DICT",key);
    }

    @SuppressWarnings("unused")
    public Object text(String key){
        return registryService.getString("@DICT",key);
    }
}
