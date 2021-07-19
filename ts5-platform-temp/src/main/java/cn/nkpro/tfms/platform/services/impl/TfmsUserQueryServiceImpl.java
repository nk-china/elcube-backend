package cn.nkpro.tfms.platform.services.impl;

import cn.nkpro.tfms.platform.mappers.gen.SysUserSavedQueryMapper;
import cn.nkpro.tfms.platform.model.po.SysUserSavedQuery;
import cn.nkpro.tfms.platform.model.po.SysUserSavedQueryExample;
import cn.nkpro.ts5.supports.GUID;
import cn.nkpro.ts5.utils.SecurityUtilz;
import cn.nkpro.ts5.config.security.TfmsUserDetails;
import cn.nkpro.tfms.platform.services.TfmsUserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by bean on 2020/7/22.
 */
@Service
public class TfmsUserQueryServiceImpl implements TfmsUserQueryService {

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
