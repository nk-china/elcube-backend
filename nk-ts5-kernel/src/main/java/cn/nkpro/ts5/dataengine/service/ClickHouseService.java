package cn.nkpro.ts5.dataengine.service;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.data.clickhouse.ClickHouseTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@ConditionalOnBean(ClickHouseTemplate.class)
@Service
public class ClickHouseService {

    @Autowired
    private ClickHouseTemplate jdbcTemplate;

    public PageList get(){
        return new PageList<>(jdbcTemplate.queryForList("select * from hits_v1 limit 1"),0,1, 1L);
    }
}
