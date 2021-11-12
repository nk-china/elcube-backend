package cn.nkpro.ts5.dataengine.service;

import cn.nkpro.ts5.platform.gen.*;
import cn.nkpro.ts5.platform.model.SysUserDashboardBO;
import cn.nkpro.ts5.security.SecurityUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {

    @Autowired
    private UserDashboardMapper userDashboardMapper;
    @Autowired
    private UserDashboardRefMapper userDashboardRefMapper;

    public SysUserDashboardBO loadUserDashboards(String dashboardId){

        SysUserDashboardBO board = new SysUserDashboardBO();

        UserDashboardRefExample example = new UserDashboardRefExample();
        example.createCriteria()
                .andAccountIdEqualTo(SecurityUtilz.getUser().getId());
        example.setOrderByClause("ORDER_BY");

        board.setRefs(userDashboardRefMapper.selectByExample(example));

        if(!board.getRefs().isEmpty()){
            board.setActiveDashboard(userDashboardMapper.selectByPrimaryKey(StringUtils.defaultIfBlank(dashboardId,board.getRefs().get(0).getId())));
        }

        return board;
    }

    @Transactional
    public void doUpdateRefs(List<UserDashboardRef> refs) {

        UserDashboardRefExample example = new UserDashboardRefExample();
        example.createCriteria().andAccountIdEqualTo(SecurityUtilz.getUser().getId());
        List<UserDashboardRef> exists = userDashboardRefMapper.selectByExample(example);

        refs.forEach(item->{

            if(StringUtils.isBlank(item.getId())){
                item.setId(UUID.randomUUID().toString());
                item.setOrderBy(refs.indexOf(item));
                item.setAccountId(SecurityUtilz.getUser().getId());
                userDashboardRefMapper.insert(item);

                UserDashboard dashboard = new UserDashboard();
                dashboard.setId(item.getId());
                dashboard.setName(item.getName());
                dashboard.setAccountId(SecurityUtilz.getUser().getId());
                dashboard.setUpdatedTime(DateTimeUtilz.nowSeconds());
                userDashboardMapper.insert(dashboard);

            }else{
                exists.stream().filter(e -> StringUtils.equals(e.getId(), item.getId())).findFirst().ifPresent(exists::remove);

                item.setOrderBy(refs.indexOf(item));
                userDashboardRefMapper.updateByPrimaryKey(item);
            }
        });

        exists.forEach(item->{
            userDashboardRefMapper.deleteByPrimaryKey(item);
            userDashboardMapper.deleteByPrimaryKey(item.getId());
        });

    }

    @Transactional
    public void doUpdate(UserDashboard dashboard){
        if(StringUtils.isBlank(dashboard.getId())){
            dashboard.setId(UUID.randomUUID().toString());
            dashboard.setAccountId(SecurityUtilz.getUser().getId());
            userDashboardMapper.insert(dashboard);

            UserDashboardRef ref = new UserDashboardRef();
            ref.setAccountId(SecurityUtilz.getUser().getId());
            ref.setId(dashboard.getId());
            ref.setOrderBy(0);
            ref.setName(dashboard.getName());
            userDashboardRefMapper.insert(ref);
        }else{
            userDashboardMapper.updateByPrimaryKeyWithBLOBs(dashboard);
        }
    }

    @Transactional
    public void doDel(String dashboardId){

        UserDashboardRefExample example = new UserDashboardRefExample();
        example.createCriteria()
                .andIdEqualTo(dashboardId);
        userDashboardRefMapper.deleteByExample(example);

        userDashboardMapper.deleteByPrimaryKey(dashboardId);
    }
}
