package cn.nkpro.tfms.platform.custom.dashboard;

import cn.nkpro.tfms.platform.custom.TfmsDashboardCard;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("nk-dashboard-pie2")
public class NkDashboardCardPie2 extends TfmsDashboardCard<Map> {

    @Getter
    private final String name = "订单概要";

    @Override
    public Map getData() {
        return null;
    }
}
