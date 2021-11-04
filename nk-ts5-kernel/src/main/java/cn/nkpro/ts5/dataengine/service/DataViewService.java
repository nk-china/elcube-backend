package cn.nkpro.ts5.dataengine.service;

import cn.nkpro.ts5.platform.gen.DataView;
import cn.nkpro.ts5.platform.gen.DataViewExample;
import cn.nkpro.ts5.platform.gen.DataViewMapper;
import cn.nkpro.ts5.platform.gen.DataViewWithBLOBs;
import cn.nkpro.ts5.security.SecurityUtilz;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class DataViewService {

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
        return dataViewMapper.selectByExampleWithBLOBs(example);
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
    public void doDel(String viewId){
        dataViewMapper.deleteByPrimaryKey(viewId);
    }
}
