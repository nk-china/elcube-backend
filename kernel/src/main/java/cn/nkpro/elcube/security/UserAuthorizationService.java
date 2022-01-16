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
package cn.nkpro.elcube.security;

import cn.nkpro.elcube.platform.gen.UserAccount;
import cn.nkpro.elcube.security.bo.GrantedAuthority;
import cn.nkpro.elcube.security.bo.UserGroupBO;
import cn.nkpro.elcube.security.gen.AuthGroup;
import cn.nkpro.elcube.security.gen.AuthLimit;
import cn.nkpro.elcube.security.gen.AuthPermission;

import java.util.List;

public interface UserAuthorizationService {



    Integer GROUP_TO_ACCOUNT = 0;
    Integer GROUP_TO_PERM = 1;


    List<GrantedAuthority> buildGrantedPerms(UserAccount account);

    List<AuthLimit> getLimits(String[] limitIds);

    AuthLimit getLimitDetail(String limitId);

    void updateLimit(AuthLimit limit);

    void removeLimit(String limitId);

    List<AuthPermission> getPerms();

    AuthPermission getPermDetail(String permId);

    void updatePerm(AuthPermission perm);

    void removePerm(String permId);

    List<AuthGroup> getGroups();

    List<UserGroupBO> getGroupBOs();

    UserGroupBO getGroupDetail(String groupId);

    UserGroupBO getGroupDetailByKey(String groupKey);

    void updateGroup(UserGroupBO group);

    void removeGroup(String groupId);

    Boolean checkGroupKey(UserGroupBO group);

    void removeAccountFromGroup(String groupId, String accountId);

    void addAccountFromGroup(String groupId, String accountId);

    List<UserAccount> accounts(String keyword);

}
