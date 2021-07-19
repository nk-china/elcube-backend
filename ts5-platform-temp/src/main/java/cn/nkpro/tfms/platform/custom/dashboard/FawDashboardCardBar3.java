package cn.nkpro.tfms.platform.custom.dashboard;

import cn.nkpro.tfms.platform.custom.TfmsDashboardCard;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("faw-dashboard-bar3")
public class FawDashboardCardBar3 extends TfmsDashboardCard<Map> {

    @Getter
    private final String name = "逾期统计";


    @Override
    public Map getData() {
        return null;
    }
}
