package cn.nkpro.ts5.platform.service;

import cn.nkpro.ts5.platform.gen.PlatformRegistry;
import cn.nkpro.ts5.platform.service.impl.PlatformRegistryServiceImpl;

import java.util.List;

public interface PlatformRegistryService {

    Object getJSON(String regType, String regKey);

    List<Object> getList(String regType, String regKeyPrefix);

    PlatformRegistry getValue(String key);

    void updateValue(PlatformRegistry registry);

    void deleteValue(PlatformRegistry registry);

    List<PlatformRegistry> getAllByType(String type);

    List<PlatformRegistryServiceImpl.TreeNode> getTree();

    void doUpdate(List<PlatformRegistry> list);
}
