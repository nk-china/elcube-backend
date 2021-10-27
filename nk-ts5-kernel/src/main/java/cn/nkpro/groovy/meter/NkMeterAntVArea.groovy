package cn.nkpro.groovy.meter


import cn.nkpro.ts5.docengine.NkDocSearchService
import cn.nkpro.ts5.platform.dashboard.NkAbstractMeter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("NkMeterAntVArea")
class NkMeterAntVArea extends NkAbstractMeter<List> {

    @Autowired
    NkDocSearchService searchService

    @Override
    String getName() {
        return "面积图"
    }

    @Override
    List getData(Object config) {
        def sql = (config as Map).get("sql") as String
        return sql ? searchService.searchBySql(sql).toList() : []
    }
}
