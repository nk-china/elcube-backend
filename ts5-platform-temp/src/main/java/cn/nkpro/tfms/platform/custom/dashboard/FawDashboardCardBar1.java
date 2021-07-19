package cn.nkpro.tfms.platform.custom.dashboard;

import cn.nkpro.tfms.platform.custom.TfmsDashboardCard;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("faw-dashboard-bar1")
public class FawDashboardCardBar1 extends TfmsDashboardCard<Map> {

    @Getter
    private final String name = "项目分类";


    @Override
    public Map getData() {
        return null;
    }
}
