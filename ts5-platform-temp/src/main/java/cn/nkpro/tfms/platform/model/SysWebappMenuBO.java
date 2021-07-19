package cn.nkpro.tfms.platform.model;

import cn.nkpro.tfms.platform.model.po.SysWebappMenu;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by bean on 2020/1/3.
 */

public class SysWebappMenuBO extends SysWebappMenu {

    @Getter@Setter
    private List<SysWebappMenu> children;
}
