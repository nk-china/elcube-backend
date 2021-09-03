package cn.nkpro.ts5.platform.model;

import cn.nkpro.ts5.platform.mybatis.gen.SysWebappMenu;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by bean on 2020/1/3.
 */

public class WebMenuBO extends SysWebappMenu {

    @Getter@Setter
    private List<SysWebappMenu> children;
}
