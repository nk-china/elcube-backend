/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.platform.controller;

import cn.nkpro.elcube.data.redis.RedisSupport;
import cn.nkpro.elcube.annotation.NkNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by bean on 2020/7/17.
 */
@NkNote("23.[DevOps]缓存管理")
@RestController
@RequestMapping("/ops/cache")
@PreAuthorize("hasAnyAuthority('*:*','DEVOPS:*','DEVOPS:CACHE')")
public class DevOpsCacheController {

    @Autowired@SuppressWarnings("all")
    private RedisSupport redisSupport;

    @NkNote("1.清空缓存")
    @RequestMapping(value = "/clear")
    public void init() {
        redisSupport.clear();
    }
}
