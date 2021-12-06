/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.security;

import cn.nkpro.elcard.basic.PageList;
import cn.nkpro.elcard.platform.gen.UserAccount;
import cn.nkpro.elcard.security.bo.UserAccountBO;
import cn.nkpro.elcard.security.bo.UserDetails;
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
}
