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

import cn.nkpro.elcube.basic.PageList;
import cn.nkpro.elcube.platform.gen.UserAccount;
import cn.nkpro.elcube.security.bo.UserAccountBO;
import cn.nkpro.elcube.security.bo.UserDetails;
import cn.nkpro.elcube.security.validate.NkAppSource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2019/12/30.
 */
public interface UserAccountService extends UserDetailsService {

    UserAccount getAccountById(String id);

    List<UserAccount> getAccountsByObjectId(List<String> docIds);

    UserAccountBO getAccount(String username, boolean preClear);

    void clear();

    void checkPasswordStrategyAndSha1(UserAccount account);

    void doChangePassword(String accountId, String oldPassword, String newPassword);

    Map<String,Object> createToken();

    Map<String, Object> refreshToken();

    UserDetails loadUserByUsernameFromCache(String username) throws UsernameNotFoundException;

    PageList<UserAccount> accountsPage(Integer from, Integer size, String orderField, String order, String keyword);

    UserAccountBO update(UserAccountBO account);

    void clearLoginLock(UserAccountBO user);

    Map<String,Object> appLogin(String phone, String verCode, String openId, String appleId);

    UserDetails getAccountByMobileTerminal(String phone, String openId, String appleId);

    UserAccountBO getAccountById(String accountId, boolean preClear);

    UserDetails loadUserByAccountId(String accountId);

    Map<String,Object> createTokenMobileTerminal();

    Map<String, Object> refreshTokenMobileTerminal();

}
