package cn.nkpro.ts5.docengine.service;

import cn.nkpro.ts5.docengine.gen.ConstantDef;

import java.util.List;

public interface NkConstantService {
    List<ConstantDef> getAll();

    void doUpdate(List<ConstantDef> list);
}
