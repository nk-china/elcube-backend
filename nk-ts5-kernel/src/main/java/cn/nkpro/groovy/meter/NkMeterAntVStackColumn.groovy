package cn.nkpro.groovy.meter


import cn.nkpro.ts5.dataengine.meter.NkAbstractEqlMeter
import org.springframework.stereotype.Component

@Component("NkMeterAntVStackColumn")
class NkMeterAntVStackColumn extends NkAbstractEqlMeter {

    @Override
    String getName() {
        return "堆叠柱状图"
    }
}
