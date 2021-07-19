package cn.nkpro.tfms.platform.custom.dashboard;

import cn.nkpro.tfms.platform.custom.TfmsDashboardCard;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("faw-dashboard-bar2")
public class FawDashboardCardBar2 extends TfmsDashboardCard<Map> {

    @Getter
    private final String name = "待办任务";


    @Override
    public Map getData() {
        return null;
    }
}
