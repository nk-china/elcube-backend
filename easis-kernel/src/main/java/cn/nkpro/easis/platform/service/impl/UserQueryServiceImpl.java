package cn.nkpro.easis.platform.service.impl;

import cn.nkpro.easis.basic.GUID;
import cn.nkpro.easis.platform.gen.UserSavedQuery;
import cn.nkpro.easis.platform.gen.UserSavedQueryExample;
import cn.nkpro.easis.platform.gen.UserSavedQueryMapper;
import cn.nkpro.easis.platform.service.UserQueryService;
import cn.nkpro.easis.security.SecurityUtilz;
import cn.nkpro.easis.security.bo.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by bean on 2020/7/22.
 */
@Service
public class UserQueryServiceImpl implements UserQueryService {

    @Autowired@SuppressWarnings("all")
    private GUID guid;

    @Autowired@SuppressWarnings("all")
    private UserSavedQueryMapper queryMapper;

    @Override
    public List<UserSavedQuery> getList(String source){
        UserDetails user = SecurityUtilz.getUser();
        UserSavedQueryExample example = new UserSavedQueryExample();
        example.createCriteria().andSourceEqualTo(source)
                .andUserIdEqualTo(user.getId());
        example.setOrderByClause("NAME");
        return queryMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public void create(UserSavedQuery query){
        UserDetails user = SecurityUtilz.getUser();
        query.setId(guid.nextId(UserSavedQuery.class));
        query.setUserId(user.getId());
        queryMapper.insertSelective(query);
    }

    @Override
    public void delete(String queryId){
        queryMapper.deleteByPrimaryKey(queryId);
    }
}
