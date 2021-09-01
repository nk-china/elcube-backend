package cn.nkpro.ts5.controller.devops;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.engine.doc.service.impl.NkDocEngineIndexService;
import cn.nkpro.ts5.engine.elasticearch.SearchEngine;
import cn.nkpro.ts5.engine.elasticearch.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by bean on 2020/7/17.
 */
@WsDocNote("23.[DevOps]缓存管理")
@RestController
@RequestMapping("/ops/cache")
@PreAuthorize("hasAnyAuthority('*:*','DEVOPS:*','DEVOPS:ES')")
public class SysCacheManagerController {

    @Autowired@SuppressWarnings("all")
    private RedisSupport redisSupport;

    @WsDocNote("1.清空缓存")
    @RequestMapping(value = "/clear")
    public void init() {
        redisSupport.clear();
    }
}
