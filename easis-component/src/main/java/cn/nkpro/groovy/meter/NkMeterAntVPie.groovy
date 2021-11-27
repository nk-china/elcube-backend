package cn.nkpro.groovy.meter

import cn.nkpro.easis.co.meter.NkAbstractEqlMeter
import org.springframework.stereotype.Component

@Component("NkMeterAntVPie")
class NkMeterAntVPie extends NkAbstractEqlMeter {

    @Override
    String getName() {
        return "饼图"
    }
}
