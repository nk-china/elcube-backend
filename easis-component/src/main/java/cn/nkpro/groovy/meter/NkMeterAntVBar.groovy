package cn.nkpro.groovy.meter


import cn.nkpro.easis.co.meter.NkAbstractEqlMeter
import org.springframework.stereotype.Component

@Component("NkMeterAntVBar")
class NkMeterAntVBar extends NkAbstractEqlMeter {

    @Override
    String getName() {
        return "条形图"
    }
}
