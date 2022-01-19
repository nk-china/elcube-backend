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
package cn.nkpro.elcube.platform.service;

import cn.nkpro.elcube.security.bo.UserAccountBO;

/**
 * @Author: wf
 * @Description 用户操作及授权接口
 * @date 2022/1/14 18:46
 */
public interface NkAccountOperationService {

    /**
     * 创建用户账号
     * @param phone
     * @param openId
     * @param appleId
     * @return
     */
    UserAccountBO createAccount(String phone, String openId, String appleId);

    /**
     * 用户组添加用户账号
     * @param accountId
     */
    void addAccountFromGroup(String accountId);
}
