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

import cn.nkpro.elcube.platform.gen.UserAccountExtend;
import cn.nkpro.elcube.platform.service.NkAbstractAccountOperation;
import cn.nkpro.elcube.security.UserAccountExtendService;
import cn.nkpro.elcube.security.UserAuthorizationService;
import cn.nkpro.elcube.security.bo.UserAccountBO;
import cn.nkpro.elcube.utils.DateTimeUtilz;
import cn.nkpro.elcube.utils.UUIDHexGenerator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: wf
 * @Description 用户操作及授权实现类
 * @date 2022/1/14 18:51
 */
public class NkAccountOperationServiceImpl extends NkAbstractAccountOperation {

    @Autowired @SuppressWarnings("all")
    private UserAuthorizationService authorizationService;

    @Autowired@SuppressWarnings("all")
    private UserAccountExtendService userAccountExtendService;

    @Override
    public UserAccountBO createAccount(String phone, String openId, String appleId) {
        UserAccountBO user = new UserAccountBO();
        user.setId(UUIDHexGenerator.generate());
        user.setUsername(phone);
        //todo 生成随机包含大小写，数字或符号的密码
        user.setPassword("Wsad123$");
        user.setCreatedTime(DateTimeUtilz.nowSeconds());
        user.setLocked(0);
        user.setValidFrom("00000000");
        user.setValidTo("20991231");
        user.setRealname("移动端用户");

        UserAccountExtend userAccountExtend = new UserAccountExtend();
        userAccountExtend.setId(UUIDHexGenerator.generate());
        userAccountExtend.setAccountId(user.getId());
        userAccountExtend.setPhone(phone);
        userAccountExtend.setOpenid(openId);
        userAccountExtend.setAppleid(appleId);
        userAccountExtend.setCreatedTime(DateTimeUtilz.nowSeconds());
        userAccountExtendService.addUserAccountExtend(userAccountExtend);
        return user;
    }

    @Override
    public void addAccountFromGroup(String accountId) {
        String groupId = "f2c7242f-a9ba-4bf4-b95b-8392c9888b19";
        authorizationService.addAccountFromGroup(groupId, accountId);
    }
}
