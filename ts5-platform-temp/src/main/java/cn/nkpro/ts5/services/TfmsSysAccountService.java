package cn.nkpro.ts5.services;

import cn.nkpro.ts5.model.SystemAccountBO;
import cn.nkpro.ts5.model.mb.gen.SysAccount;

import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2019/12/30.
 */
public interface TfmsSysAccountService {

    SysAccount getAccountById(String id);

    List<SysAccount> getAccountsByObjectId(List<String> docIds);

    SystemAccountBO getAccount(String username, boolean preClear);

    void clear();

    void checkPasswordStrategyAndSha1(SysAccount account);

    void doChangePassword(String accountId, String oldPassword, String newPassword);

    Map<String,Object> createToken();

    Map<String, Object> refreshToken();
}
