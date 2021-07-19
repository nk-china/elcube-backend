package cn.nkpro.tfms.platform.services;

import cn.nkpro.tfms.platform.model.SysWebappMenuBO;
import cn.nkpro.tfms.platform.model.po.SysWebappMenu;

import java.util.List;

/**
 * Created by bean on 2020/1/3.
 */
public interface TfmsSysWebappMenuService {
    List<SysWebappMenuBO> getMenus(boolean filterAuth);

    SysWebappMenu getDetail(String id);

    void doUpdate(List<SysWebappMenuBO> menus);
}
