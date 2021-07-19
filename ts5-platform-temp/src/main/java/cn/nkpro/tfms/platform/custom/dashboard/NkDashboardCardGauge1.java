package cn.nkpro.tfms.platform.custom.dashboard;

import cn.nkpro.tfms.platform.custom.TfmsDashboardCard;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("nk-dashboard-gauge1")
public class NkDashboardCardGauge1 extends TfmsDashboardCard<Map> {

    @Getter
    private final String name = "今日关注";

    @Override
    public Map getData() {
        return null;
    }
}
