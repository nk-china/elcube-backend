package cn.nkpro.ts5.model;

import cn.nkpro.ts5.model.mb.gen.SysWebappMenu;
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
