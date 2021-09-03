package cn.nkpro.ts5.platform.service.impl;

import cn.nkpro.ts5.basic.GUID;
import cn.nkpro.ts5.platform.gen.SysUserSavedQuery;
import cn.nkpro.ts5.platform.gen.SysUserSavedQueryExample;
import cn.nkpro.ts5.platform.gen.SysUserSavedQueryMapper;
import cn.nkpro.ts5.security.SecurityUtilz;
import cn.nkpro.ts5.security.bo.UserDetails;
import cn.nkpro.ts5.platform.service.UserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by bean on 2020/7/22.
 */
@Service
public class UserQueryServiceImpl implements UserQueryService {

    @Autowired
    private GUID guid;

    @Autowired
    private SysUserSavedQueryMapper queryMapper;

    @Override
    public List<SysUserSavedQuery> getList(String source){
        UserDetails user = SecurityUtilz.getUser();
        SysUserSavedQueryExample example = new SysUserSavedQueryExample();
        example.createCriteria().andSourceEqualTo(source)
                .andUserIdEqualTo(user.getId());
        example.setOrderByClause("NAME");
        return queryMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public void create(SysUserSavedQuery query){
        UserDetails user = SecurityUtilz.getUser();
        query.setId(guid.nextId(SysUserSavedQuery.class));
        query.setUserId(user.getId());
        queryMapper.insertSelective(query);
    }

    @Override
    public void delete(String queryId){
        queryMapper.deleteByPrimaryKey(queryId);
    }
}
