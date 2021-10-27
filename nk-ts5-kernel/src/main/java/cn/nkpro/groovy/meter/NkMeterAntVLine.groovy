package cn.nkpro.groovy.meter

import cn.nkpro.ts5.docengine.NkDocSearchService;
import cn.nkpro.ts5.platform.dashboard.NkAbstractMeter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("NkMeterAntVLine")
class NkMeterAntVLine extends NkAbstractMeter<List> {

    @Autowired
    NkDocSearchService searchService

    @Override
    String getName() {
        return "折线图"
    }

    @Override
    List getData(Object config) {
        return searchService.searchBySql((config as Map).get("sql") as String).toList();
    }
}
