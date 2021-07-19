package cn.nkpro.tfms.platform.services;

import cn.nkpro.tfms.platform.model.po.DefConstant;

import java.util.List;

public interface TfmsDefConstantService {
    List<DefConstant> getAll();

    void doUpdate(List<DefConstant> list);
}
