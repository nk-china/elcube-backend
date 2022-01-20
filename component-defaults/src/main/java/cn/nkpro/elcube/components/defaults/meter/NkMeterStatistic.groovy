package cn.nkpro.elcube.components.defaults.meter

import cn.nkpro.elcube.co.meter.NkAbstractEqlMeter
import org.springframework.stereotype.Component

@Component("NkMeterStatistic")
class NkMeterStatistic extends NkAbstractEqlMeter {
    @Override
    String getName() {
        return "统计数值"
    }
}
