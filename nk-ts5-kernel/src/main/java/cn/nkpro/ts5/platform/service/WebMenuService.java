package cn.nkpro.ts5.platform.service;

import cn.nkpro.ts5.platform.model.WebMenuBO;
import cn.nkpro.ts5.platform.mybatis.gen.SysWebappMenu;

import java.util.List;

/**
 * Created by bean on 2020/1/3.
 */
public interface WebMenuService {
    List<WebMenuBO> getMenus(boolean filterAuth);

    SysWebappMenu getDetail(String id);

    void doUpdate(List<WebMenuBO> menus);
}
