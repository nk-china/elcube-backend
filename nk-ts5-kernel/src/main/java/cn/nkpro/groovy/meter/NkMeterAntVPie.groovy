package cn.nkpro.groovy.meter;

import cn.nkpro.ts5.platform.dashboard.NkAbstractMeter
import org.springframework.stereotype.Component

@Component("NkMeterAntVPie")
class NkMeterAntVPie extends NkAbstractMeter<Map> {

    @Override
    String getName() {
        return "饼图"
    }

    @Override
    Map getData() {
        return null
    }
}
