package cn.nkpro.easis.platform.service;

import cn.nkpro.easis.platform.gen.PlatformMenu;
import cn.nkpro.easis.platform.model.WebMenuBO;

import java.util.List;

/**
 * Created by bean on 2020/1/3.
 */
public interface WebMenuService {
    List<WebMenuBO> getMenus(boolean filterAuth);

    PlatformMenu getDetail(String id);

    void doUpdate(List<WebMenuBO> menus);
}
