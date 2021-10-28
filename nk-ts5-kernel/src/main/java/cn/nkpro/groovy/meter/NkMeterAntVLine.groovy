package cn.nkpro.groovy.meter


import cn.nkpro.ts5.platform.dashboard.NkAbstractEqlMeter
import org.springframework.stereotype.Component

@Component("NkMeterAntVLine")
class NkMeterAntVLine extends NkAbstractEqlMeter {

    @Override
    String getName() {
        return "折线图"
    }
}
