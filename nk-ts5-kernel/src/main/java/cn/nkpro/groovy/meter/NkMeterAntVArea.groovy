package cn.nkpro.groovy.meter


import cn.nkpro.ts5.dataengine.meter.NkAbstractEqlMeter
import org.springframework.stereotype.Component

@Component("NkMeterAntVArea")
class NkMeterAntVArea extends NkAbstractEqlMeter {

    @Override
    String getName() {
        return "面积图"
    }
}
