package cn.nkpro.tfms.platform.custom.dashboard;

import cn.nkpro.tfms.platform.custom.TfmsDashboardCard;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("nk-dashboard-scatter1")
public class NkDashboardCardScatter1 extends TfmsDashboardCard<Map> {

    @Getter
    private final String name = "逾期";

    @Override
    public Map getData() {
        return null;
    }
}
