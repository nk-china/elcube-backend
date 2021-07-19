package cn.nkpro.tfms.platform.custom.dashboard;

import cn.nkpro.tfms.platform.custom.TfmsDashboardCard;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("nk-dashboard-calendar1")
public class NkDashboardCardCalendar1 extends TfmsDashboardCard<Map> {

    @Getter
    private final String name = "审批中";

    @Override
    public Map getData() {
        return null;
    }
}
