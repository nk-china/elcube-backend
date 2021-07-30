package cn.nkpro.ts5.engine.doc.service;

import cn.nkpro.ts5.orm.mb.gen.ConstantDef;

import java.util.List;

public interface NkConstantService {
    List<ConstantDef> getAll();

    void doUpdate(List<ConstantDef> list);
}
