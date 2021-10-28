package cn.nkpro.groovy.meter


import cn.nkpro.ts5.platform.dashboard.NkAbstractEqlMeter
import org.springframework.stereotype.Component

@Component("NkMeterAntVBar")
class NkMeterAntVBar extends NkAbstractEqlMeter {

    @Override
    String getName() {
        return "条形图"
    }
}
