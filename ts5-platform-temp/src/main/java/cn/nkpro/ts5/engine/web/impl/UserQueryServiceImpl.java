package cn.nkpro.ts5.engine.web.impl;

import cn.nkpro.ts5.config.security.TfmsUserDetails;
import cn.nkpro.ts5.model.mb.gen.SysUserSavedQuery;
import cn.nkpro.ts5.model.mb.gen.SysUserSavedQueryExample;
import cn.nkpro.ts5.model.mb.gen.SysUserSavedQueryMapper;
import cn.nkpro.ts5.engine.web.UserQueryService;
import cn.nkpro.ts5.supports.GUID;
import cn.nkpro.ts5.utils.SecurityUtilz;
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
        TfmsUserDetails user = SecurityUtilz.getUser();
        SysUserSavedQueryExample example = new SysUserSavedQueryExample();
        example.createCriteria().andSourceEqualTo(source)
                .andUserIdEqualTo(user.getId());
        example.setOrderByClause("NAME");
        return queryMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public void create(SysUserSavedQuery query){
        TfmsUserDetails user = SecurityUtilz.getUser();
        query.setId(guid.nextId(SysUserSavedQuery.class));
        query.setUserId(user.getId());
        queryMapper.insertSelective(query);
    }

    @Override
    public void delete(String queryId){
        queryMapper.deleteByPrimaryKey(queryId);
    }
}
