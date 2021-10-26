package cn.nkpro.groovy.meter;

import cn.nkpro.ts5.platform.dashboard.NkAbstractMeter
import org.springframework.stereotype.Component

@Component("NkMeterAntVLine")
class NkMeterAntVLine extends NkAbstractMeter<Map> {

    @Override
    String getName() {
        return "折线图"
    }

    @Override
    Map getData() {
        return null
    }
}
