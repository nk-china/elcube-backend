package cn.nkpro.ts5.platform.service;

import cn.nkpro.ts5.platform.gen.PlatformRegistry;
import cn.nkpro.ts5.platform.service.impl.PlatformRegistryServiceImpl;

import java.util.List;

public interface PlatformRegistryService {

    Object getJSON(String regType, String regKey);

    PlatformRegistry getValue(String key);

    void updateValue(PlatformRegistry registry);

    void deleteValue(PlatformRegistry registry);

    List<PlatformRegistry> getAll();

    List<PlatformRegistryServiceImpl.TreeNode> getTree();

    void doUpdate(List<PlatformRegistry> list);
}
