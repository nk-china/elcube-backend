package cn.nkpro.easis.platform.model;

import cn.nkpro.easis.platform.gen.PlatformMenu;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by bean on 2020/1/3.
 */

public class WebMenuBO extends PlatformMenu {

    @Getter@Setter
    private List<PlatformMenu> children;
}
