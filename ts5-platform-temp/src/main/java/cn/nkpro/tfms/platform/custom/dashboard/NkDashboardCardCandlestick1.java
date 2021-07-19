package cn.nkpro.tfms.platform.custom.dashboard;

import cn.nkpro.tfms.platform.custom.TfmsDashboardCard;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("nk-dashboard-candlestick1")
public class NkDashboardCardCandlestick1 extends TfmsDashboardCard<Map> {

    @Getter
    private final String name = "任务";

    @Getter
    private final int w = 8;

    @Override
    public Map getData() {
        return null;
    }
}
