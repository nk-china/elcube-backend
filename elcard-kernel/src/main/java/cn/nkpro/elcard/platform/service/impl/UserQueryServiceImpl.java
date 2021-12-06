/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.platform.service.impl;

import cn.nkpro.elcard.basic.GUID;
import cn.nkpro.elcard.platform.gen.UserSavedQuery;
import cn.nkpro.elcard.platform.gen.UserSavedQueryExample;
import cn.nkpro.elcard.platform.gen.UserSavedQueryMapper;
import cn.nkpro.elcard.platform.service.UserQueryService;
import cn.nkpro.elcard.security.SecurityUtilz;
import cn.nkpro.elcard.security.bo.UserDetails;
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
