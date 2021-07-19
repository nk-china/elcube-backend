package cn.nkpro.tfms.platform.model;

import cn.nkpro.tfms.platform.model.po.DefDocComponent;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by bean on 2020/7/14.
 */
public class DefDocComponentBO extends DefDocComponent {

    @Getter@Setter
    private String dataComponent;
    @Getter@Setter
    private String[] dataComponentExtNames;
    @Getter@Setter
    private String[] pageComponentNames;
    @Getter@Setter
    private String[] defComponentNames;
    @Getter@Setter
    private Boolean writeable = true;

    @Override
    public String getComponentMapping() {
        return StringUtils.defaultIfBlank(super.getComponentMapping(),super.getComponent());
    }
}
