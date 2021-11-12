package cn.nkpro.ts5.security.impl;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.basic.GUID;
import cn.nkpro.ts5.data.redis.RedisSupport;
import cn.nkpro.ts5.platform.gen.UserAccount;
import cn.nkpro.ts5.platform.gen.UserAccountExample;
import cn.nkpro.ts5.platform.gen.UserAccountMapper;
import cn.nkpro.ts5.security.UserAuthorizationService;
import cn.nkpro.ts5.security.UserBusinessAdapter;
import cn.nkpro.ts5.security.bo.GrantedAuthority;
import cn.nkpro.ts5.security.bo.UserGroupBO;
import cn.nkpro.ts5.security.gen.*;
import cn.nkpro.ts5.spel.NkSpELManager;
import cn.nkpro.ts5.utils.BeanUtilz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserAuthorizationServiceImpl implements UserAuthorizationService {

    @Autowired@SuppressWarnings("all")
    private GUID guid;
    @Autowired@SuppressWarnings("all")
    private AuthGroupMapper authGroupMapper;
    @Autowired@SuppressWarnings("all")
    private AuthGroupRefMapper authGroupRefMapper;
    @Autowired@SuppressWarnings("all")
    private AuthPermissionMapper authPermissionMapper;
    @Autowired@SuppressWarnings("all")
    private AuthLimitMapper authLimitMapper;
    @Autowired@SuppressWarnings("all")
    private UserAccountMapper accountMapper;

    @Autowired@SuppressWarnings("all")
    private RedisSupport<UserGroupBO> redisSupport;
    @Autowired@SuppressWarnings("all")
    private RedisSupport<AuthLimit> redisSupportLimit;
    @Autowired@SuppressWarnings("all")
    private NkSpELManager spELManager;
    @Autowired@SuppressWarnings("all")
    private UserBusinessAdapter userBusinessAdapter;


    /**
     * 创建指定账号的权限集合
     * @param accountId 账号ID
     * @return List<NkGrantedAuthority>
     */
    @Override
    public List<GrantedAuthority> buildGrantedPerms(String accountId, String partnerId){

        Object user = userBusinessAdapter.getUser(accountId);

        // 构造权限列表
        List<GrantedAuthority> permList = new ArrayList<>();

        AuthGroupRefExample authGroupRefExample = new AuthGroupRefExample();
        authGroupRefExample.createCriteria()
                .andRefIdEqualTo(accountId)
                .andRefTypeEqualTo(GROUP_TO_ACCOUNT);

        authGroupRefMapper.selectByExample(authGroupRefExample)
                .forEach((ref->{
                    UserGroupBO group = buildUserGroup(ref.getGroupId());
                    if(group!=null&&group.getAuthorities()!=null){
                        permList.addAll(group.getAuthorities());
                    }
                }));

        // 处理limit
        Set<String> limitIds = new HashSet<>();
        permList.forEach(grantedAuthority -> {
            if(grantedAuthority.getLimitIds()!=null){
                limitIds.addAll(Arrays.asList(grantedAuthority.getLimitIds()));
            }
        });

        Map<String,AuthLimit> limits = limitIds.stream()
                .map(limitId->
                    redisSupportLimit.getIfAbsent(Constants.CACHE_AUTH_LIMIT,limitId,
                            ()-> authLimitMapper.selectByPrimaryKey(limitId))
                ).collect(Collectors.toMap(AuthLimit::getLimitId,v->v));

        permList.forEach(authority -> {
            if(authority.getLimitIds()!=null){
                List<String> query = Arrays.stream(authority.getLimitIds())
                        .map(limits::get)
                        .filter(limit->limit!=null && limit.getContent()!=null)
                        .map(AuthLimit::getContent)
                        .map(limit->spELManager.convert(limit,user))
                        .collect(Collectors.toList());
                if(query.size()>1){
                    authority.setLimitQuery(
                            query.stream()
                                    .collect(Collectors.joining(",", "{\"bool\":{\"must\":[", "]}}"))
                    );
                }else if(query.size()==1){
                    authority.setLimitQuery(query.get(0));
                }
            }
        });

        permList.forEach(perm->
            permList.stream().filter(e -> StringUtils.equals(e.getPermResource(), perm.getPermResource()))
                    .findFirst()
                    .ifPresent(first-> perm.setDisabled(perm!=first))
        );

        return permList.stream()
                    .sorted()
                    .collect(Collectors.toList());
    }

    /**
     * 创建一个用户组模型
     * @param groupId 用户组Id
     * @return 用户组
     */
    private UserGroupBO buildUserGroup(String groupId){

        return redisSupport.getIfAbsent(Constants.CACHE_AUTH_GROUP,groupId,()->{

            AuthGroup sysAuthGroup = authGroupMapper.selectByPrimaryKey(groupId);

            if(sysAuthGroup!=null){

                UserGroupBO g = BeanUtilz.copyFromObject(sysAuthGroup, UserGroupBO.class);

                AuthGroupRefExample authGroupRefExample = new AuthGroupRefExample();
                authGroupRefExample.createCriteria()
                        .andGroupIdEqualTo(groupId)
                        .andRefTypeEqualTo(GROUP_TO_PERM);

                List<String> permIds = authGroupRefMapper.selectByExample(authGroupRefExample)
                        .stream()
                        .map(AuthGroupRefKey::getRefId)
                        .collect(Collectors.toList());

                if(!permIds.isEmpty()){

                    // 查询Group下的权限定义
                    AuthPermissionExample authPermissionExample = new AuthPermissionExample();
                    authPermissionExample.createCriteria()
                            .andPermIdIn(permIds);
                    authPermissionExample.setOrderByClause("PERM_DESC");

                    g.setPermissions(authPermissionMapper.selectByExampleWithBLOBs(authPermissionExample));

                    // 创建Group下的授权模型
                    List<GrantedAuthority> authoritys = new ArrayList<>();
                    g.getPermissions()
                        .forEach(permission -> {
                            if(StringUtils.startsWith(permission.getPermResource(),Constants.BIZ_DEFAULT_EMPTY)
                                    &&StringUtils.containsAny(permission.getPermResource(),'|',',')){
                                // 多单据权限
                                authoritys.addAll(
                                    Arrays.stream(
                                        permission.getPermResource().substring(1).split("[|,]")
                                    )
                                    .map(resource->
                                        buildAuthority(StringUtils.join(Constants.BIZ_DEFAULT_EMPTY,resource),permission,g)
                                    )
                                    .collect(Collectors.toList())
                                );
                            }else{
                                // 单单据及其他权限
                                authoritys.add(
                                    buildAuthority(permission.getPermResource(),permission,g)
                                );
                            }
                        });

                    g.setAuthorities(authoritys.stream().sorted().collect(Collectors.toList()));
                }
                return g;
            }
            return null;
        });
    }

    /**
     * 创建一个授权对象
     * @param resource resource
     * @param perm perm
     * @param group group
     * @return NkGrantedAuthority
     */
    private GrantedAuthority buildAuthority(String resource, AuthPermission perm, AuthGroup group){
        GrantedAuthority authority = new GrantedAuthority();
        authority.setPermResource(resource);
        authority.setPermOperate(perm.getPermOperate());
        authority.setSubResource(perm.getSubResource());
        authority.setLimitIds(StringUtils.split(perm.getLimitId(),'|'));
        authority.setLevel(perm.getPermLevel());
        authority.setFromPermissionId(perm.getPermId());
        authority.setFromPermissionDesc(perm.getPermDesc());
        authority.setFromGroupId(group.getGroupId());
        authority.setFromGroupDesc(group.getGroupDesc());
        authority.setAuthority(String.format("%s:%s",authority.getPermResource(),authority.getPermOperate()));
        return authority;
    }

    //// 以下为管理代码

    @Override
    public List<AuthLimit> getLimits(String[] limitIds){
        AuthLimitExample example = new AuthLimitExample();
        if(ArrayUtils.isNotEmpty(limitIds))
            example.createCriteria().andLimitIdIn(Arrays.asList(limitIds));
        example.setOrderByClause("LIMIT_DESC");
        return authLimitMapper.selectByExample(example);
    }

    @Override
    public AuthLimit getLimitDetail(String limitId){
        if(StringUtils.isNotBlank(limitId)){
            return redisSupportLimit.getIfAbsent(Constants.CACHE_AUTH_LIMIT,limitId,
                ()-> authLimitMapper.selectByPrimaryKey(limitId));
        }
        return null;
    }
    @Override
    public void updateLimit(AuthLimit limit){
        Assert.hasText(limit.getLimitDesc(),"限制描述不能为空");

        QueryBuilders.wrapperQuery(limit.getContent());

        if(StringUtils.isBlank(limit.getLimitId())){
            limit.setLimitId(guid.nextId(AuthLimit.class));
            authLimitMapper.insert(limit);
        }else{
            authLimitMapper.updateByPrimaryKeyWithBLOBs(limit);
            redisSupportLimit.deleteHash(Constants.CACHE_AUTH_LIMIT,limit.getLimitId());
        }
    }
    @Override
    public void removeLimit(String limitId){
        authLimitMapper.deleteByPrimaryKey(limitId);
        redisSupportLimit.deleteHash(Constants.CACHE_AUTH_LIMIT,limitId);
    }


    @Override
    public List<AuthPermission> getPerms(){
        AuthPermissionExample example = new AuthPermissionExample();
        example.setOrderByClause("PERM_DESC");
        return authPermissionMapper.selectByExample(example);
    }

    @Override
    public AuthPermission getPermDetail(String permId){
        return authPermissionMapper.selectByPrimaryKey(permId);
    }
    @Override
    public void updatePerm(AuthPermission perm){
        Assert.hasText(perm.getPermDesc(),"权限描述不能为空");
        Assert.hasText(perm.getPermResource(),"权限资源不能为空");
        Assert.hasText(perm.getPermOperate(),"权限操作不能为空");

        if(StringUtils.isNotBlank(perm.getLimitId())){
            perm.setLimitId(
                Arrays.stream(StringUtils.split(perm.getLimitId(),'|'))
                    .sorted()
                    .collect(Collectors.joining("|"))
            );
        }

        if(StringUtils.isBlank(perm.getPermId())){
            perm.setPermId(guid.nextId(AuthPermission.class));
            authPermissionMapper.insert(perm);
        }else{
            authPermissionMapper.updateByPrimaryKeyWithBLOBs(perm);
            clearGroupByPerm(perm.getPermId());
        }
    }
    @Override
    public void removePerm(String permId){

        Assert.isTrue(!permId.startsWith("nk-default-"),"系统权限不可移除");

        authPermissionMapper.deleteByPrimaryKey(permId);
        clearGroupByPerm(permId);
    }

    private void clearGroupByPerm(String permId){

        AuthGroupRefExample authGroupRefExample = new AuthGroupRefExample();
        authGroupRefExample.createCriteria()
                .andRefIdEqualTo(permId)
                .andRefTypeEqualTo(GROUP_TO_PERM);

        Object[] groupIds = authGroupRefMapper.selectByExample(authGroupRefExample)
                .stream()
                .map(AuthGroupRefKey::getGroupId)
                .distinct()
                .toArray(String[]::new);

        if(groupIds.length>0){
            redisSupport.deleteHash(Constants.CACHE_AUTH_GROUP,groupIds);
        }

    }



    @Override
    public List<AuthGroup> getGroups(){
        AuthGroupExample example = new AuthGroupExample();
        example.setOrderByClause("GROUP_DESC");
        return authGroupMapper.selectByExample(example);
    }

    @Override
    public List<UserGroupBO> getGroupBOs(){
        AuthGroupExample example = new AuthGroupExample();
        example.setOrderByClause("GROUP_DESC");
        return authGroupMapper.selectByExample(example).stream().map(g->buildUserGroup(g.getGroupId())).collect(Collectors.toList());
    }

    @Override
    public UserGroupBO getGroupDetail(String groupId){

        UserGroupBO group = buildUserGroup(groupId);

        // 查询用户组下的账号
        AuthGroupRefExample authGroupRefExample = new AuthGroupRefExample();
        authGroupRefExample.createCriteria()
                .andGroupIdEqualTo(groupId)
                .andRefTypeEqualTo(GROUP_TO_ACCOUNT);

        List<String> accountIds = authGroupRefMapper.selectByExample(authGroupRefExample)
                .stream()
                .map(AuthGroupRefKey::getRefId)
                .collect(Collectors.toList());

        if(!accountIds.isEmpty()){

            UserAccountExample accountExample = new UserAccountExample();
            accountExample.createCriteria()
                    .andIdIn(accountIds);
            accountExample.setOrderByClause("USERNAME");

            group.setAccounts(
                    accountMapper.selectByExample(accountExample)
                            .stream()
                            .peek(a -> a.setPassword(null))
                            .collect(Collectors.toList()));
        }

        return group;
    }
    @Override
    public void updateGroup(UserGroupBO group){


        Assert.isTrue(StringUtils.isBlank(group.getGroupId()) || !group.getGroupId().startsWith("nk-default-"),"系统用户组不可更新");
        Assert.hasText(group.getGroupDesc(),"权限描述不能为空");

        if(StringUtils.isBlank(group.getGroupId())){
            group.setGroupId(guid.nextId(AuthGroup.class));
            authGroupMapper.insert(group);
        }else{
            authGroupMapper.updateByPrimaryKey(group);

            AuthGroupRefExample example = new AuthGroupRefExample();
            example.createCriteria()
                    .andGroupIdEqualTo(group.getGroupId())
                    .andRefTypeEqualTo(GROUP_TO_PERM);
            authGroupRefMapper.deleteByExample(example);
        }

        if(group.getPermissions()!=null){
            group.getPermissions()
                    .forEach(perm->{
                        AuthGroupRefKey ref = new AuthGroupRefKey();
                        ref.setGroupId(group.getGroupId());
                        ref.setRefId(perm.getPermId());
                        ref.setRefType(GROUP_TO_PERM);
                        authGroupRefMapper.insert(ref);

                    });
        }

        redisSupport.deleteHash(Constants.CACHE_AUTH_GROUP,group.getGroupId());
    }
    @Override
    public void removeGroup(String groupId){

        Assert.isTrue(!groupId.startsWith("nk-default-"),"系统用户组不可删除");

        AuthGroupRefExample example = new AuthGroupRefExample();
        example.createCriteria().andGroupIdEqualTo(groupId);
        authGroupRefMapper.deleteByExample(example);

        authGroupMapper.deleteByPrimaryKey(groupId);

        redisSupport.deleteHash(Constants.CACHE_AUTH_GROUP,groupId);
    }

    @Override
    public void removeAccountFromGroup(String groupId, String accountId){

        Assert.isTrue(!(groupId.startsWith("nk-default-") && accountId.startsWith("nk-default-")),"系统用户不可移除");

        AuthGroupRefExample example = new AuthGroupRefExample();
        example.createCriteria()
                .andGroupIdEqualTo(groupId)
                .andRefIdEqualTo(accountId)
                .andRefTypeEqualTo(GROUP_TO_ACCOUNT);
        authGroupRefMapper.deleteByExample(example);
    }

    @Override
    public void addAccountFromGroup(String groupId, String accountId){
        removeAccountFromGroup(groupId,accountId);

        AuthGroupRefKey ref = new AuthGroupRefKey();
        ref.setGroupId(groupId);
        ref.setRefId(accountId);
        ref.setRefType(GROUP_TO_ACCOUNT);
        authGroupRefMapper.insert(ref);
    }

    @Override
    public List<UserAccount> accounts(String keyword){
        UserAccountExample example = new UserAccountExample();
        example.createCriteria()
                .andUsernameLike(String.format("%%%s%%",keyword));

        return accountMapper.selectByExample(example,new RowBounds(0,10))
                .stream()
                .peek(a -> a.setPassword(null))
                .collect(Collectors.toList());
    }
}
