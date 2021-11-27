package cn.nkpro.groovy.meter


import cn.nkpro.easis.co.meter.NkAbstractEqlMeter
import org.springframework.stereotype.Component

@Component("NkMeterAntVLine")
class NkMeterAntVLine extends NkAbstractEqlMeter {

    @Override
    String getName() {
        return "折线图"
    }
}
