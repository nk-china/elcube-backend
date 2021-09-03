package cn.nkpro.ts5.platform.controller;

import cn.nkpro.ts5.redis.RedisSupport;
import cn.nkpro.ts5.wsdoc.annotation.WsDocNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by bean on 2020/7/17.
 */
@WsDocNote("23.[DevOps]缓存管理")
@RestController
@RequestMapping("/ops/cache")
@PreAuthorize("hasAnyAuthority('*:*','DEVOPS:*','DEVOPS:CACHE')")
public class DevOpsCacheController {

    @Autowired@SuppressWarnings("all")
    private RedisSupport redisSupport;

    @WsDocNote("1.清空缓存")
    @RequestMapping(value = "/clear")
    public void init() {
        redisSupport.clear();
    }
}
