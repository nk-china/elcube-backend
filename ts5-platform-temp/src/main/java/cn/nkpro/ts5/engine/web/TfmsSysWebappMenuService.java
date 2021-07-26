package cn.nkpro.ts5.engine.web;

import cn.nkpro.ts5.model.SysWebappMenuBO;
import cn.nkpro.ts5.model.mb.gen.SysWebappMenu;

import java.util.List;

/**
 * Created by bean on 2020/1/3.
 */
public interface TfmsSysWebappMenuService {
    List<SysWebappMenuBO> getMenus(boolean filterAuth);

    SysWebappMenu getDetail(String id);

    void doUpdate(List<SysWebappMenuBO> menus);
}
