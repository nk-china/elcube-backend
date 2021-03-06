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
package cn.nkpro.elcube.bigdata.service;

import cn.nkpro.elcube.bigdata.gen.DataView;
import cn.nkpro.elcube.bigdata.gen.DataViewExample;
import cn.nkpro.elcube.bigdata.gen.DataViewMapper;
import cn.nkpro.elcube.bigdata.gen.DataViewWithBLOBs;
import cn.nkpro.elcube.security.SecurityUtilz;
import cn.nkpro.elcube.utils.UUIDHexGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
            view.setId(UUIDHexGenerator.generate());
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
