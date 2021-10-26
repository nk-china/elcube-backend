package cn.nkpro.groovy.meter;

import cn.nkpro.ts5.platform.dashboard.NkAbstractMeter
import org.springframework.stereotype.Component

@Component("NkMeterAntVColumn")
class NkMeterAntVColumn extends NkAbstractMeter<Map> {

    @Override
    String getName() {
        return "柱状图"
    }

    @Override
    Map getData() {
        return null
    }
}
