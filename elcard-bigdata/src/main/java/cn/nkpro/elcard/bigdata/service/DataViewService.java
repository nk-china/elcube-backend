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
package cn.nkpro.elcard.bigdata.service;

import cn.nkpro.elcard.bigdata.gen.DataView;
import cn.nkpro.elcard.bigdata.gen.DataViewExample;
import cn.nkpro.elcard.bigdata.gen.DataViewMapper;
import cn.nkpro.elcard.bigdata.gen.DataViewWithBLOBs;
import cn.nkpro.elcard.security.SecurityUtilz;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class DataViewService {

    @SuppressWarnings("all")
    @Autowired
    private DataViewMapper dataViewMapper;

    public List<? extends DataView> getAll(){

        DataViewExample example = new DataViewExample();
        example.createCriteria()
                .andAccountIdEqualTo(SecurityUtilz.getUser().getId());
        example.setOrderByClause("ORDER_BY");

        return dataViewMapper.selectByExampleWithBLOBs(example);
    }

    public DataView getDetail(String id){
        return dataViewMapper.selectByPrimaryKey(id);
    }

    public List<DataViewWithBLOBs> getDetail(List<String> id){
        if(CollectionUtils.isEmpty(id)){
            return Collections.emptyList();
        }
        DataViewExample example = new DataViewExample();
        example.createCriteria().andIdIn(id);
        List<DataViewWithBLOBs> list = dataViewMapper.selectByExampleWithBLOBs(example);

        list.sort(Comparator.comparingInt(a -> id.indexOf(a.getId())));

        return list;
    }

    @Transactional
    public void doUpdate(DataViewWithBLOBs view){
        if(StringUtils.isBlank(view.getId())){
            view.setId(UUID.randomUUID().toString());
            view.setAccountId(SecurityUtilz.getUser().getId());
            dataViewMapper.insert(view);
        }else{
            dataViewMapper.updateByPrimaryKeyWithBLOBs(view);
        }
    }

    @Transactional
    public void doUpdate(List<String> ids){
        ids.forEach(id->{
            DataViewWithBLOBs view = new DataViewWithBLOBs();
            view.setId(id);
            view.setOrderBy(ids.indexOf(id));
            dataViewMapper.updateByPrimaryKeySelective(view);
        });
    }

    @Transactional
    public void doDel(String viewId){
        dataViewMapper.deleteByPrimaryKey(viewId);
    }
}
