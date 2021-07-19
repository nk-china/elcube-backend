package cn.nkpro.tfms.platform.custom.dashboard;

import cn.nkpro.tfms.platform.custom.TfmsDashboardCard;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("nk-dashboard-bar2")
public class NkDashboardCardBar2 extends TfmsDashboardCard<Map> {

    @Getter
    private final String name = "坏账";

    @Override
    public Map getData() {
        return null;
    }
}
