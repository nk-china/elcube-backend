package cn.nkpro.ts5.services.impl;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.basic.TfmsSpELManager;
import cn.nkpro.ts5.config.security.TfmsGrantedAuthority;
import cn.nkpro.ts5.config.security.TfmsUserDetails;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.exception.TfmsAccessDeniedException;
import cn.nkpro.ts5.model.SysAuthGroupBO;
import cn.nkpro.ts5.model.mb.gen.*;
import cn.nkpro.ts5.services.TfmsPermService;
import cn.nkpro.ts5.supports.GUID;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.SecurityUtilz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TfmsPermServiceImpl implements TfmsPermService {

    @Autowired
    private GUID guid;
    @Autowired
    private SysAuthGroupMapper authGroupMapper;
    @Autowired
    private SysAuthGroupRefMapper authGroupRefMapper;
    @Autowired
    private SysAuthPermissionMapper authPermissionMapper;
    @Autowired
    private SysAuthLimitMapper authLimitMapper;
    @Autowired
    private SysAccountMapper accountMapper;

    @Autowired
    private RedisSupport<SysAuthGroupBO> redisSupport;
    @Autowired
    private RedisSupport<SysAuthLimit> redisSupportLimit;
    @Autowired
    private TfmsSpELManager spELManager;


    /**
     * 创建指定账号的权限集合
     * @param accountId 账号ID
     * @return
     */
    @Override
    public List<TfmsGrantedAuthority> buildGrantedPerms(String accountId,String partnerId){

        //DocHV partner = StringUtils.isNotBlank(partnerId)?docEngine.getDocDetail(partnerId):new DocHV();
        DocHV partner = new DocHV();

        // 构造权限列表
        List<TfmsGrantedAuthority> permList = new ArrayList<>();

        SysAuthGroupRefExample authGroupRefExample = new SysAuthGroupRefExample();
        authGroupRefExample.createCriteria()
                .andRefIdEqualTo(accountId)
                .andRefTypeEqualTo(GROUP_TO_ACCOUNT);

        authGroupRefMapper.selectByExample(authGroupRefExample)
                .forEach((ref->{
                    SysAuthGroupBO group = buildUserGroup(ref.getGroupId());
                    if(group!=null&&group.getAuthorities()!=null){
                        permList.addAll(group.getAuthorities());
                    }
                }));

        // 处理limit
        Set<String> limitIds = new HashSet<>();
        permList.forEach(tfmsGrantedAuthority -> {
            if(tfmsGrantedAuthority.getLimitIds()!=null){
                limitIds.addAll(Arrays.asList(tfmsGrantedAuthority.getLimitIds()));
            }
        });

        Map<String,SysAuthLimit> limits = limitIds.stream()
                .map(limitId->
                    redisSupportLimit.getIfAbsent(Constants.CACHE_AUTH_LIMIT,limitId,
                            ()-> authLimitMapper.selectByPrimaryKey(limitId))
                ).collect(Collectors.toMap(SysAuthLimit::getLimitId,v->v));

        permList.forEach(authority -> {
            if(authority.getLimitIds()!=null){
                List<String> querys = Arrays.stream(authority.getLimitIds())
                        .map(limits::get)
                        .filter(limit->limit!=null && limit.getContent()!=null)
                        .map(SysAuthLimit::getContent)
                        .map(limit->spELManager.convert(partner,limit))
                        .collect(Collectors.toList());
                if(querys.size()>1){
                    authority.setLimitQuery(
                            querys.stream()
                                    .collect(Collectors.joining(",", "{\"bool\":{\"must\":[", "]}}"))
                    );
                }else if(querys.size()==1){
                    authority.setLimitQuery(querys.get(0));
                }
            }
        });

        return permList.stream()
                    .sorted()
                    .collect(Collectors.toList());
    }

//    /**
//     * 当前账号下 根据授权过滤单据卡片
//     * @param mode 操作
//     * @param runtimeDefined 单据类型运行时对象
//     * @return
//     */
//    @Override
//    public DefDocTypeBO filterDocCards(String mode, DefDocTypeBO runtimeDefined){
//
//        // 移除没有权限的卡片
//        getDocAuthorities(mode,runtimeDefined.getDocType())
//                .stream()
//                .findFirst()
//                .map(authority -> StringUtils.split(authority.getSubResource(),'|'))
//                .ifPresent(authority->{
//                    runtimeDefined.getCustomComponents()
//                            .removeIf(component ->!ArrayUtils.contains(authority,component.getComponent()));
//                });
//
//        // 设置卡片的writeable值
////        runtimeDefined.getCustomComponents()
////                .forEach(component -> component.setWriteable(true));
//        getDocAuthorities(TfmsPermService.MODE_WRITE, runtimeDefined.getDocType())
//                .stream()
//                .findFirst()
//                .map(writeAuthority -> StringUtils.split(writeAuthority.getSubResource(),'|'))
//                .ifPresent(writeAuthority -> {
//                    runtimeDefined.getCustomComponents()
//                            .stream()
//                            .filter(DefDocComponentBO::getWriteable)
//                            .forEach(component -> component.setWriteable(
//                                    ArrayUtils.contains(writeAuthority,component.getComponent())
//                            ));
//                });
//
//        return runtimeDefined;
//    }

    /**
     * 当前账号下 创建一个doc权限过滤器
     * @param mode
     * @param docType 允许为空，查询所有
     * @return
     */
    @Override
    public BoolQueryBuilder buildDocFilter(String mode,String docType,String typeKey, boolean ignoreLimit){
        // 处理权限
        List<TfmsGrantedAuthority> authorities = getDocAuthorities(mode, docType);

        BoolQueryBuilder filter = QueryBuilders.boolQuery();

        if(authorities.isEmpty()){
            filter.must(QueryBuilders.idsQuery().addIds("NONE"));
            return filter;
        }

        // 这里优化了下，把相同limit的单据类型条件合并
        Map<String,TfmsGrantedAuthority> distinct = new HashMap<>();
        authorities.forEach(authority -> {
            distinct.putIfAbsent(authority.getDocType(),authority);
        });

        Map<String,List<String>> collectByLimit = new HashMap<>();

        distinct.forEach((type,authority)-> {
            String limit = ignoreLimit?null:authority.getLimitQuery();
            collectByLimit.putIfAbsent(limit,new ArrayList<>());
            collectByLimit.get(limit).add(authority.getDocType());
        });

        collectByLimit.forEach((limit,docTypes)->{
            BoolQueryBuilder builder = QueryBuilders.boolQuery();

            if(!docTypes.contains("*"))
                builder.must(QueryBuilders.termsQuery(StringUtils.defaultIfBlank(typeKey,"docType"), docTypes));
            if(StringUtils.isNotBlank(limit))
                builder.must(QueryBuilders.wrapperQuery(limit));
            filter.should(builder);
        });

        log.debug("单据查询权限过滤:\n"+filter);

        return filter;
    }

//    /**
//     * 当前账号下 检查是否具有对指定单据的权限
//     * @param mode 操作
//     * @param docType 单据类型
//     */
//    @Override
//    public void assertHasDocPerm(String mode, String docType){
//        if(!hasDocPerm(mode,docType)){
//            throw new TfmsAccessDeniedException(String.format("没有单据类型[%s]-[%s]访问权限",docType, mode));
//        }
//    }
//
//    /**
//     * 当前账号下 检查是否具有对指定单据类型的权限
//     * @param mode 操作
//     * @param docType 单据类型
//     * @return
//     */
//    @Override
//    public boolean hasDocPerm(String mode, String docType){
//        return !getDocAuthorities(mode,docType).isEmpty();
//    }
//
//    /**
//     * 当前账号下 检查是否具有对指定单据的权限
//     * @param mode 操作
//     * @param docId 单据ID
//     * @param docType 允许为空，查询所有
//     * @throws TfmsAccessDeniedException
//     */
//    @Override
//    public void assertHasDocPerm(String mode, String docId, String docType){
//        if(!hasDocPerm(mode,docId,docType)){
//            if(docType!=null)
//                throw new TfmsAccessDeniedException(String.format("没有单据[%s:%s]-[%s]的访问权限", docType, docId, mode));
//            else
//                throw new TfmsAccessDeniedException(String.format("没有单据[%s]-[%s]的访问权限", docId, mode));
//        }
//    }
//    /**
//     * 当前账号下 检查是否具有对指定单据ID的权限
//     * @param mode 操作
//     * @param docId 单据ID
//     * @param docType 单据类型
//     * @return
//     */
//    @Override
//    public boolean hasDocPerm(String mode, String docId, String docType) {
//        if(SecurityUtilz.getAuthorities().stream()
//                .anyMatch(g-> g.getAuthority().equals("*:*"))){
//            return true;
//        }
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
//                .postFilter(buildDocFilter(mode, docType,null,false))
//                .query(QueryBuilders.termQuery("docId",docId));
//        try {
//            return searchEngine.exists(IndexDoc.class,sourceBuilder);
//        } catch (IOException e) {
//            throw new TfmsSearchException(e);
//        }
//    }


    /**
     * 当前账号下 获取指定单据类型的授权模型对象
     * @param mode 操作
     * @param docType 单据类型
     * @return 授权模型对象集合
     */
    private List<TfmsGrantedAuthority> getDocAuthorities(String mode, String docType){
        TfmsUserDetails user = SecurityUtilz.getUser();
        if(user!=null){
            return user.getAuthorities()
                    .stream()
                    .filter(authority->
                            authority.getPermResource().equals("*")||
                                    authority.getPermResource().equals("@*")||
                                    authority.getPermResource().equalsIgnoreCase(String.format("@%s", docType))||
                                    (docType==null && authority.getPermResource().startsWith("@"))
                    )
                    .filter(authority->StringUtils.equalsAnyIgnoreCase(authority.getPermOperate(),"*",mode))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 创建一个用户组模型
     * @param groupId 用户组Id
     * @return 用户组
     */
    private SysAuthGroupBO buildUserGroup(String groupId){

        return redisSupport.getIfAbsent(Constants.CACHE_AUTH_GROUP,groupId,()->{

            SysAuthGroup sysAuthGroup = authGroupMapper.selectByPrimaryKey(groupId);

            if(sysAuthGroup!=null){

                SysAuthGroupBO g = BeanUtilz.copyFromObject(sysAuthGroup,SysAuthGroupBO.class);

                SysAuthGroupRefExample authGroupRefExample = new SysAuthGroupRefExample();
                authGroupRefExample.createCriteria()
                        .andGroupIdEqualTo(groupId)
                        .andRefTypeEqualTo(GROUP_TO_PERM);

                List<String> permIds = authGroupRefMapper.selectByExample(authGroupRefExample)
                        .stream()
                        .map(SysAuthGroupRefKey::getRefId)
                        .collect(Collectors.toList());

                if(!permIds.isEmpty()){

                    // 查询Group下的权限定义
                    SysAuthPermissionExample authPermissionExample = new SysAuthPermissionExample();
                    authPermissionExample.createCriteria()
                            .andPermIdIn(permIds);
                    authPermissionExample.setOrderByClause("PERM_DESC");

                    g.setPermissions(authPermissionMapper.selectByExampleWithBLOBs(authPermissionExample));

                    // 创建Group下的授权模型
                    List<TfmsGrantedAuthority> authoritys = new ArrayList<>();
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
     * @param resource
     * @param perm
     * @param group
     * @return
     */
    private TfmsGrantedAuthority buildAuthority(String resource,SysAuthPermission perm,SysAuthGroup group){
        TfmsGrantedAuthority authority = new TfmsGrantedAuthority();
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
    public List<SysAuthLimit> getLimits(String[] limitIds){
        SysAuthLimitExample example = new SysAuthLimitExample();
        if(ArrayUtils.isNotEmpty(limitIds))
            example.createCriteria().andLimitIdIn(Arrays.asList(limitIds));
        example.setOrderByClause("LIMIT_DESC");
        return authLimitMapper.selectByExample(example);
    }

    @Override
    public SysAuthLimit getLimitDetail(String limitId){
        if(StringUtils.isNotBlank(limitId)){
            return redisSupportLimit.getIfAbsent(Constants.CACHE_AUTH_LIMIT,limitId,
                ()-> authLimitMapper.selectByPrimaryKey(limitId));
        }
        return null;
    }
    @Override
    public void updateLimit(SysAuthLimit limit){
        Assert.hasText(limit.getLimitDesc(),"限制描述不能为空");

        QueryBuilders.wrapperQuery(limit.getContent());

        if(StringUtils.isBlank(limit.getLimitId())){
            limit.setLimitId(guid.nextId(SysAuthLimit.class));
            authLimitMapper.insert(limit);
        }else{
            authLimitMapper.updateByPrimaryKeyWithBLOBs(limit);
            redisSupportLimit.delete(Constants.CACHE_AUTH_LIMIT,limit.getLimitId());
        }
    }
    @Override
    public void removeLimit(String limitId){
        authLimitMapper.deleteByPrimaryKey(limitId);
        redisSupportLimit.delete(Constants.CACHE_AUTH_LIMIT,limitId);
    }


    @Override
    public List<SysAuthPermission> getPerms(){
        SysAuthPermissionExample example = new SysAuthPermissionExample();
        example.setOrderByClause("PERM_DESC");
        return authPermissionMapper.selectByExample(example);
    }

    @Override
    public SysAuthPermission getPermDetail(String permId){
        return authPermissionMapper.selectByPrimaryKey(permId);
    }
    @Override
    public void updatePerm(SysAuthPermission perm){
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
            perm.setPermId(guid.nextId(SysAuthPermission.class));
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

        SysAuthGroupRefExample authGroupRefExample = new SysAuthGroupRefExample();
        authGroupRefExample.createCriteria()
                .andRefIdEqualTo(permId)
                .andRefTypeEqualTo(GROUP_TO_PERM);

        Object[] groupIds = authGroupRefMapper.selectByExample(authGroupRefExample)
                .stream()
                .map(SysAuthGroupRefKey::getGroupId)
                .distinct()
                .toArray(String[]::new);

        if(groupIds.length>0){
            redisSupport.delete(Constants.CACHE_AUTH_GROUP,groupIds);
        }

    }



    @Override
    public List<SysAuthGroup> getGroups(){
        SysAuthGroupExample example = new SysAuthGroupExample();
        example.setOrderByClause("GROUP_DESC");
        return authGroupMapper.selectByExample(example);
    }

    @Override
    public SysAuthGroupBO getGroupDetail(String groupId){

        SysAuthGroupBO group = buildUserGroup(groupId);

        // 查询用户组下的账号
        SysAuthGroupRefExample authGroupRefExample = new SysAuthGroupRefExample();
        authGroupRefExample.createCriteria()
                .andGroupIdEqualTo(groupId)
                .andRefTypeEqualTo(GROUP_TO_ACCOUNT);

        List<String> accountIds = authGroupRefMapper.selectByExample(authGroupRefExample)
                .stream()
                .map(SysAuthGroupRefKey::getRefId)
                .collect(Collectors.toList());

        if(!accountIds.isEmpty()){

            SysAccountExample accountExample = new SysAccountExample();
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
    public void updateGroup(SysAuthGroupBO group){


        Assert.isTrue(StringUtils.isBlank(group.getGroupId()) || !group.getGroupId().startsWith("nk-default-"),"系统用户组不可更新");
        Assert.hasText(group.getGroupDesc(),"权限描述不能为空");

        if(StringUtils.isBlank(group.getGroupId())){
            group.setGroupId(guid.nextId(SysAuthGroup.class));
            authGroupMapper.insert(group);
        }else{
            authGroupMapper.updateByPrimaryKey(group);

            SysAuthGroupRefExample example = new SysAuthGroupRefExample();
            example.createCriteria()
                    .andGroupIdEqualTo(group.getGroupId())
                    .andRefTypeEqualTo(GROUP_TO_PERM);
            authGroupRefMapper.deleteByExample(example);
        }

        if(group.getPermissions()!=null){
            group.getPermissions()
                    .forEach(perm->{
                        SysAuthGroupRefKey ref = new SysAuthGroupRefKey();
                        ref.setGroupId(group.getGroupId());
                        ref.setRefId(perm.getPermId());
                        ref.setRefType(GROUP_TO_PERM);
                        authGroupRefMapper.insert(ref);

                    });
        }

        redisSupport.delete(Constants.CACHE_AUTH_GROUP,group.getGroupId());
    }
    @Override
    public void removeGroup(String groupId){

        Assert.isTrue(!groupId.startsWith("nk-default-"),"系统用户组不可删除");

        SysAuthGroupRefExample example = new SysAuthGroupRefExample();
        example.createCriteria().andGroupIdEqualTo(groupId);
        authGroupRefMapper.deleteByExample(example);

        authGroupMapper.deleteByPrimaryKey(groupId);

        redisSupport.delete(Constants.CACHE_AUTH_GROUP,groupId);
    }

    @Override
    public void removeAccountFromGroup(String groupId, String accountId){

        Assert.isTrue(!(groupId.startsWith("nk-default-") && accountId.startsWith("nk-default-")),"系统用户不可移除");

        SysAuthGroupRefExample example = new SysAuthGroupRefExample();
        example.createCriteria()
                .andGroupIdEqualTo(groupId)
                .andRefIdEqualTo(accountId)
                .andRefTypeEqualTo(GROUP_TO_ACCOUNT);
        authGroupRefMapper.deleteByExample(example);
    }

    @Override
    public void addAccountFromGroup(String groupId, String accountId){
        removeAccountFromGroup(groupId,accountId);

        SysAuthGroupRefKey ref = new SysAuthGroupRefKey();
        ref.setGroupId(groupId);
        ref.setRefId(accountId);
        ref.setRefType(GROUP_TO_ACCOUNT);
        authGroupRefMapper.insert(ref);
    }

    @Override
    public List<SysAccount> accounts(String keyword){
        SysAccountExample example = new SysAccountExample();
        example.createCriteria()
                .andUsernameLike(String.format("%%%s%%",keyword));

        return accountMapper.selectByExample(example,new RowBounds(0,10))
                .stream()
                .peek(a -> a.setPassword(null))
                .collect(Collectors.toList());
    }
}
