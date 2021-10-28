package cn.nkpro.ts5.platform.dashboard;

import cn.nkpro.ts5.data.redis.RedisSupport;
import cn.nkpro.ts5.docengine.NkDocSearchService;
import cn.nkpro.ts5.security.SecurityUtilz;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class NkAbstractEqlMeter extends NkAbstractMeter<List>{

    @Autowired
    private RedisSupport<List> redisSupport;
    @Autowired
    private NkDocSearchService searchService;

    @Override
    public List getData(Object config) {
        String sql = (String) ((Map)config).get("sql");

        if(StringUtils.isBlank(sql)){
            return Collections.emptyList();
        }

        String key = SecurityUtilz.getUser().getId() +':'+ DigestUtils.md5DigestAsHex(sql.getBytes());
        List list = redisSupport.getIfAbsent(key, () -> searchService.searchBySql(sql).toList());

        redisSupport.expire(key,60);

        return list;
    }
}
