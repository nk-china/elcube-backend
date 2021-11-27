package cn.nkpro.groovy.meter


import cn.nkpro.easis.co.meter.NkAbstractEqlMeter
import org.springframework.stereotype.Component

@Component("NkMeterAntVWaterfall")
class NkMeterAntVWaterfall extends NkAbstractEqlMeter {

    @Override
    String getName() {
        return "瀑布图"
    }
}
