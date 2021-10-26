package cn.nkpro.groovy.meter;

import cn.nkpro.ts5.platform.dashboard.NkAbstractMeter
import org.springframework.stereotype.Component

@Component("NkMeterAntVArea")
class NkMeterAntVArea extends NkAbstractMeter<Map> {

    @Override
    String getName() {
        return "面积图"
    }

    @Override
    Map getData() {
        return null
    }
}
