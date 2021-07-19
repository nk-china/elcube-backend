package cn.nkpro.tfms.platform.custom.dashboard;

import cn.nkpro.tfms.platform.custom.TfmsDashboardCard;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("nk-dashboard-bar3")
public class NkDashboardCardBar3 extends TfmsDashboardCard<Map> {

    @Getter
    private final String name = "待跟进业务";

    @Override
    public Map getData() {
        return null;
    }
}
