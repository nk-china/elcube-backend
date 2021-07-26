package cn.nkpro.ts5.engine.doc.service;

import cn.nkpro.ts5.model.mb.gen.ConstantDef;

import java.util.List;

public interface NKConstantService {
    List<ConstantDef> getAll();

    void doUpdate(List<ConstantDef> list);
}
