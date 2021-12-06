/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.platform.service.impl;

import cn.nkpro.elcube.basic.GUID;
import cn.nkpro.elcube.platform.gen.UserSavedQuery;
import cn.nkpro.elcube.platform.gen.UserSavedQueryExample;
import cn.nkpro.elcube.platform.gen.UserSavedQueryMapper;
import cn.nkpro.elcube.platform.service.UserQueryService;
import cn.nkpro.elcube.security.SecurityUtilz;
import cn.nkpro.elcube.security.bo.UserDetails;
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
