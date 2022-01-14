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
package cn.nkpro.elcube.platform.service.impl;

import cn.nkpro.elcube.data.redis.RedisSupport;
import cn.nkpro.elcube.platform.service.NkAbstractMobile;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: wf
 * @Description 移动端服务实现类
 * @date 2022/1/14 15:15
 */
public class NkMobileServiceImpl extends NkAbstractMobile {

    @Autowired
    @SuppressWarnings("all")
    private RedisSupport<Object> redisSupport;

    @Override
    public String sendVerificationCode(String phone) {
        //todo 发送验证码到手机
        String verCode = "123456";
        redisSupport.set(phone,verCode);
        return verCode;
    }
}
