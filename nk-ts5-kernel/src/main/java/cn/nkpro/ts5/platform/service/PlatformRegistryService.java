package cn.nkpro.ts5.platform.service;

import cn.nkpro.ts5.platform.gen.PlatformRegistry;

import java.util.List;

public interface PlatformRegistryService {
    List<PlatformRegistry> getAll();

    void doUpdate(List<PlatformRegistry> list);
}
