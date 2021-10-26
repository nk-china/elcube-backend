package cn.nkpro.groovy.meter;

import cn.nkpro.ts5.platform.dashboard.NkAbstractMeter
import org.springframework.stereotype.Component

@Component("NkMeterAntVBar")
class NkMeterAntVBar extends NkAbstractMeter<Map> {

    @Override
    String getName() {
        return "条形图"
    }

    @Override
    Map getData() {
        return null
    }
}
