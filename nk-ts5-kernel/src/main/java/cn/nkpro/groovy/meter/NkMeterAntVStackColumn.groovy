package cn.nkpro.groovy.meter;

import cn.nkpro.ts5.platform.dashboard.NkAbstractMeter
import org.springframework.stereotype.Component

@Component("NkMeterAntVStackColumn")
class NkMeterAntVStackColumn extends NkAbstractMeter<Map> {

    @Override
    String getName() {
        return "堆叠柱状图"
    }

    @Override
    Map getData() {
        return null
    }
}
