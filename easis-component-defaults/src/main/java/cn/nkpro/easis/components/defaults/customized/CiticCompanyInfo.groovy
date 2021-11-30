package cn.nkpro.easis.components.defaults.customized

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.docengine.NkAbstractCard
import cn.nkpro.easis.docengine.model.DocDefIV
import cn.nkpro.easis.docengine.model.DocHV
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@NkNote("企业基本面")
@Component("CiticCompanyInfo")
class CiticCompanyInfo extends NkAbstractCard<Data,Map> {

    @Autowired
    JdbcTemplate jdbcTemplate

    @Override
    Data afterGetData(DocHV doc, Data data, DocDefIV defIV, Map d) {


        def map1 = jdbcTemplate.queryForMap("select * from ts5_bb_citic.public_sentiment_company_fundamentals where id = 1")
        def map2 = jdbcTemplate.queryForMap("select * from ts5_bb_citic.public_sentiment_news where id = 3")

        Data dt = new Data()
        dt.setMap1(map1)
        dt.setMap2(map2)

        return dt
    }

    @Override
    Data beforeUpdate(DocHV doc, Data data, Data original, DocDefIV defIV, Map d) {


        println data


        //jdbcTemplate.update("update ts5_bb_citic.public_sentiment_company_fundamentals up", data.getMap1().get(""))


        return null
    }

    static class Data{
        Map map1
        Map map2
    }
}
