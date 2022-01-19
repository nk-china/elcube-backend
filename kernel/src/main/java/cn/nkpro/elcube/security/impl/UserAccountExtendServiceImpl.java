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
package cn.nkpro.elcube.security.impl;

import cn.nkpro.elcube.platform.gen.UserAccountExtend;
import cn.nkpro.elcube.platform.gen.UserAccountExtendExample;
import cn.nkpro.elcube.platform.gen.UserAccountExtendMapper;
import cn.nkpro.elcube.security.UserAccountExtendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: wf
 * @Description 账号扩展信息
 * @date 2022/1/17 15:08
 */
@Component("NkSysAccountExtendService")
public class UserAccountExtendServiceImpl implements UserAccountExtendService {

    @Autowired@SuppressWarnings("all")
    private UserAccountExtendMapper userAccountExtendMapper;

    @Override
    public void addUserAccountExtend(UserAccountExtend userAccountExtend) {
        userAccountExtendMapper.insert(userAccountExtend);
    }

    @Override
    public List<UserAccountExtend> selectByExample(UserAccountExtendExample example) {
        return userAccountExtendMapper.selectByExample(example);
    }
}
