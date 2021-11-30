package cn.nkpro.easis.security;

import cn.nkpro.easis.basic.PageList;
import cn.nkpro.easis.platform.gen.UserAccount;
import cn.nkpro.easis.security.bo.UserAccountBO;
import cn.nkpro.easis.security.bo.UserDetails;
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
