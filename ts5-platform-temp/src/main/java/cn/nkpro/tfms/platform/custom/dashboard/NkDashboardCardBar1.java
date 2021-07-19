package cn.nkpro.tfms.platform.custom.dashboard;

import cn.nkpro.tfms.platform.custom.TfmsDashboardCard;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("nk-dashboard-bar1")
public class NkDashboardCardBar1 extends TfmsDashboardCard<Map> {

    @Getter
    private final String name = "客户统计";

    @Getter
    private final int w = 8;

    @Override
    public Map getData() {
        return null;
    }
}
