package cn.nkpro.groovy.meter


import cn.nkpro.ts5.co.meter.NkAbstractEqlMeter
import org.springframework.stereotype.Component

@Component("NkMeterAntVColumn")
class NkMeterAntVColumn extends NkAbstractEqlMeter {

    @Override
    String getName() {
        return "柱状图"
    }
}
