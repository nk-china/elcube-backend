package cn.nkpro.tfms.platform.custom.dashboard;

import cn.nkpro.tfms.platform.custom.TfmsDashboardCard;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("nk-dashboard-radar1")
public class NkDashboardCardRadar1 extends TfmsDashboardCard<Map> {

    @Getter
    private final String name = "雷达";

    @Override
    public Map getData() {
        return null;
    }
}
