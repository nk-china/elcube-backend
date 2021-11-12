package cn.nkpro.ts5.security.impl;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.basic.NkProperties;
import cn.nkpro.ts5.data.redis.RedisSupport;
import cn.nkpro.ts5.platform.gen.UserAccount;
import cn.nkpro.ts5.platform.gen.UserAccountExample;
import cn.nkpro.ts5.platform.gen.UserAccountMapper;
import cn.nkpro.ts5.security.HashUtil;
import cn.nkpro.ts5.security.JwtHelper;
import cn.nkpro.ts5.security.UserAccountService;
import cn.nkpro.ts5.security.UserAuthorizationService;
import cn.nkpro.ts5.security.bo.UserAccountBO;
import cn.nkpro.ts5.security.bo.UserDetails;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by bean on 2019/12/30.
 */
@Component("NkSysAccountService")
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired@SuppressWarnings("all")
    private UserAccountMapper userAccountMapper;

    @Autowired@SuppressWarnings("all")
    private RedisSupport<UserAccount> redisTemplateAccount;

    @Autowired@SuppressWarnings("all")
    private RedisSupport<UserAccountBO> redisTemplate;
    @Autowired@SuppressWarnings("all")
    private NkProperties nkProperties;

    @Autowired@SuppressWarnings("all")
    private UserAuthorizationService authorizationService;

    @Override
    public UserAccount getAccountById(String id){
        return StringUtils.isBlank(id)?null:redisTemplateAccount.getIfAbsent(Constants.CACHE_USERS,id,()-> userAccountMapper.selectByPrimaryKey(id));
    }

    public List<UserAccount> getAccountsByObjectId(List<String> docIds) {

        if(CollectionUtils.isEmpty(docIds)){
            return Collections.emptyList();
        }

        UserAccountExample example = new UserAccountExample();
        example.createCriteria()
                .andObjectIdIn(docIds);
        return userAccountMapper.selectByExample(example);
    }

    @Override
    public UserAccountBO getAccount(String username, boolean preClear) {
        if(preClear){
            redisTemplate.deleteHash(Constants.CACHE_USERS,username);
        }
        return redisTemplate.getIfAbsent(Constants.CACHE_USERS,username,()-> getAccount(username));
    }

    private UserAccountBO getAccount(String username){

        UserAccountExample example = new UserAccountExample();
        example.createCriteria().andUsernameEqualTo(username);

        return userAccountMapper.selectByExample(example)
                .stream()
                .findAny()
                .map(sysAccount -> {
                    UserAccountBO ud = BeanUtilz.copyFromObject(sysAccount, UserAccountBO.class);
                    ud.setAuthorities(authorizationService.buildGrantedPerms(sysAccount.getId(),sysAccount.getObjectId()));
                    return ud;
                })
                .orElse(null);
    }

    @Override
    public void clear(){
        redisTemplate.deleteHash(Constants.CACHE_USERS, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Override
    public void checkPasswordStrategyAndSha1(UserAccount account){
        // 检查密码是否健壮
        if(StringUtils.isNotBlank(nkProperties.getPasswordStrategy())){
            Assert.isTrue(Pattern.matches(nkProperties.getPasswordStrategy(),account.getPassword()),"密码强度不符合要求");
        }
        // 加密密码 sha1两次
        String password = account.getPassword();
        password = HashUtil.hash(password,"SHA1");
        password = HashUtil.hash(password,"SHA1");
        account.setPassword(password);
    }

    @Override
    public void doChangePassword(String accountId, String oldPassword, String newPassword){

        UserAccount exists = userAccountMapper.selectByPrimaryKey(accountId);

        oldPassword = HashUtil.hash(oldPassword,"SHA1");
        oldPassword = HashUtil.hash(oldPassword,"SHA1");

        Assert.isTrue(StringUtils.equals(exists.getPassword(),oldPassword),"原始密码错误");

        UserAccount account = new UserAccount();
        account.setId(accountId);
        account.setPassword(newPassword);

        checkPasswordStrategyAndSha1(account);

        account.setUpdatedTime(DateTimeUtilz.nowSeconds());
        userAccountMapper.updateByPrimaryKeySelective(account);
        redisTemplate.deleteHash(Constants.CACHE_USERS, exists.getUsername());
    }

    /**
     *
     * 创建一个token，有效期为30分钟，
     * 返回给前端的过期时间为15分钟，
     * token过期后的15分钟以内，用户仍然可以通过token来刷新token
     * @return token信息
     */
    @Override
    public Map<String, Object> createToken() {


        Map<String,Object> map = new HashMap<>();
        map.put("username",SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        map.put("password",SecurityContextHolder.getContext().getAuthentication().getCredentials());

        long time   = 1000L * 60 * 45;             // 有效期默认为45分钟
        long expire = 1000L * 60 * 15;             // 过期时间设定为15分钟

        String token = JwtHelper.createJWT(map,time);

        Map<String,Object> tokenInfo = new HashMap<>();
        tokenInfo.put("accessToken",token);
        tokenInfo.put("expire",expire);
        tokenInfo.put("refresh",time);

        return tokenInfo;

    }

    @Override
    public Map<String, Object> refreshToken() {
        return createToken();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(getAccount(username,true))
                .map(account-> BeanUtilz.copyFromObject(account, UserDetails.class))
                .orElse(null);
    }
    @Override
    public UserDetails loadUserByUsernameFromCache(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(getAccount(username,false))
                .map(account->BeanUtilz.copyFromObject(account, UserDetails.class))
                .orElse(null);
    }
}
