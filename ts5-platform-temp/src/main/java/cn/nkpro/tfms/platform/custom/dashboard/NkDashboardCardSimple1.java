package cn.nkpro.tfms.platform.custom.dashboard;

import cn.nkpro.tfms.platform.custom.TfmsDashboardCard;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("nk-dashboard-simple1")
public class NkDashboardCardSimple1 extends TfmsDashboardCard<Map> {

    @Getter
    private final String name = "示例";

    @Override
    public Map getData() {
        return null;
    }
}
