/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.security;

import cn.nkpro.easis.platform.gen.UserAccount;
import cn.nkpro.easis.security.bo.GrantedAuthority;
import cn.nkpro.easis.security.bo.UserGroupBO;
import cn.nkpro.easis.security.gen.AuthGroup;
import cn.nkpro.easis.security.gen.AuthLimit;
import cn.nkpro.easis.security.gen.AuthPermission;

import java.util.List;

public interface UserAuthorizationService {



    Integer GROUP_TO_ACCOUNT = 0;
    Integer GROUP_TO_PERM = 1;


    List<GrantedAuthority> buildGrantedPerms(String accountId, String partnerId);

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

    void updateGroup(UserGroupBO group);

    void removeGroup(String groupId);

    void removeAccountFromGroup(String groupId, String accountId);

    void addAccountFromGroup(String groupId, String accountId);

    List<UserAccount> accounts(String keyword);
}
