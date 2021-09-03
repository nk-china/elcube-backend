package cn.nkpro.ts5.security;

import cn.nkpro.ts5.security.bo.UserAccountBO;
import cn.nkpro.ts5.security.mybatis.gen.SysAccount;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2019/12/30.
 */
public interface UserAccountService extends UserDetailsService {

    SysAccount getAccountById(String id);

    List<SysAccount> getAccountsByObjectId(List<String> docIds);

    UserAccountBO getAccount(String username, boolean preClear);

    void clear();

    void checkPasswordStrategyAndSha1(SysAccount account);

    void doChangePassword(String accountId, String oldPassword, String newPassword);

    Map<String,Object> createToken();

    Map<String, Object> refreshToken();

    TfmsUserDetails loadUserByUsernameFromCache(String username) throws UsernameNotFoundException;
}
