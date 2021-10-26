package cn.nkpro.groovy.meter;

import cn.nkpro.ts5.platform.dashboard.NkAbstractMeter
import org.springframework.stereotype.Component

@Component("NkMeterAntVWaterfall")
class NkMeterAntVWaterfall extends NkAbstractMeter<Map> {

    @Override
    String getName() {
        return "瀑布图"
    }

    @Override
    Map getData() {
        return null
    }
}
