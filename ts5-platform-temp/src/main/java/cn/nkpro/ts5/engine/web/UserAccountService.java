package cn.nkpro.ts5.engine.web;

import cn.nkpro.ts5.engine.web.model.UserAccountBO;
import cn.nkpro.ts5.orm.mb.gen.SysAccount;

import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2019/12/30.
 */
public interface UserAccountService {

    SysAccount getAccountById(String id);

    List<SysAccount> getAccountsByObjectId(List<String> docIds);

    UserAccountBO getAccount(String username, boolean preClear);

    void clear();

    void checkPasswordStrategyAndSha1(SysAccount account);

    void doChangePassword(String accountId, String oldPassword, String newPassword);

    Map<String,Object> createToken();

    Map<String, Object> refreshToken();
}
