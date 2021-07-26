package cn.nkpro.ts5.engine.web.impl;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.engine.web.model.WebMenuBO;
import cn.nkpro.ts5.model.mb.gen.SysWebappMenu;
import cn.nkpro.ts5.model.mb.gen.SysWebappMenuExample;
import cn.nkpro.ts5.model.mb.gen.SysWebappMenuMapper;
import cn.nkpro.ts5.engine.devops.TfmsDefDeployAble;
import cn.nkpro.ts5.engine.web.WebMenuService;
import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import cn.nkpro.ts5.utils.SecurityUtilz;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/1/3.
 */
@Slf4j
@Service
public class WebMenuServiceImpl implements WebMenuService, TfmsDefDeployAble,InitializingBean {

    @Autowired
    private SysWebappMenuMapper sysWebappMenuMapper;

    @Autowired
    private RedisSupport<List<WebMenuBO>> redisSupport;

    /**
     * 根据当前用户的权限获取对应的菜单
     * @return
     */
    @Override
    public List<WebMenuBO> getMenus(boolean filterAuth){

        List<WebMenuBO> menus = redisSupport.getIfAbsent(Constants.CACHE_NAV_MENUS,()-> {

            SysWebappMenuExample example = new SysWebappMenuExample();
            example.setOrderByClause("ORDER_BY");

            List<WebMenuBO> ret = sysWebappMenuMapper.selectByExample(example).stream()
                    .map(m-> BeanUtilz.copyFromObject(m, WebMenuBO.class))
                    .collect(Collectors.toList());

            ret.stream()
                    .filter(m-> StringUtils.isNotBlank(m.getParentId()))
                    .forEach(m->
                            ret.stream()
                                    .filter(p->StringUtils.equals(p.getMenuId(),m.getParentId()))
                                    .findAny()
                                    .ifPresent(p->{
                                        if(p.getChildren()==null){
                                            p.setChildren(new ArrayList<>());
                                        }
                                        p.getChildren().add(m);
                                    })
                    );

            ret.removeIf(sysWebappMenuBO -> StringUtils.isNotBlank(sysWebappMenuBO.getParentId()));
            return ret;
        });

        if(filterAuth){

            menus.forEach(menu->{
                if(menu.getChildren()!=null){
                    menu.getChildren().removeIf(sub->!(
                            StringUtils.isBlank(sub.getAuthorityOptions())
                                    || SecurityUtilz.hasAnyAuthority(sub.getAuthorityOptions().split("[|,]"))
                    ));
                }
            });

            menus.removeIf(menu->
                    (menu.getChildren()!=null && menu.getChildren().isEmpty())
                            ||
                            !(
                                    StringUtils.isBlank(menu.getAuthorityOptions())
                                            ||SecurityUtilz.hasAnyAuthority(menu.getAuthorityOptions().split("[|,]"))
                            )
            );
        }

        return menus;
    }

    @Override
    public SysWebappMenu getDetail(String id){
        return sysWebappMenuMapper.selectByPrimaryKey(id);
    }

    @Transactional
    @Override
    public void doUpdate(List<WebMenuBO> menus){
        Long updateTime = DateTimeUtilz.nowSeconds();
        menus.forEach(menu->{
            menu.setOrderBy((menus.indexOf(menu)+1) * 10000);
            update(menu,updateTime);
            if(menu.getChildren()!=null){
                    menu.getChildren().forEach(m->{
                        m.setParentId(menu.getMenuId());
                        m.setOrderBy(menu.getOrderBy()+menu.getChildren().indexOf(m));
                        update(m,updateTime);
                    });
            }
        });

        SysWebappMenuExample example = new SysWebappMenuExample();
        example.createCriteria().andUpdatedTimeLessThan(updateTime);
        sysWebappMenuMapper.deleteByExample(example);

        redisSupport.delete(Constants.CACHE_NAV_MENUS);
    }

    private void update(SysWebappMenu menu,Long updateTime){
        menu.setUpdatedTime(updateTime);
        if(sysWebappMenuMapper.selectByPrimaryKey(menu.getMenuId())==null){
            sysWebappMenuMapper.insert(menu);
        }else if(menu.getMenuOptions()==null){
            sysWebappMenuMapper.updateByPrimaryKeySelective(menu);
        }else{
            sysWebappMenuMapper.updateByPrimaryKeyWithBLOBs(menu);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        redisSupport.delete(Constants.CACHE_NAV_MENUS);
    }

    @Override
    public int deployOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public List<SysWebappMenu> deployExport(JSONObject config) {
        if(config.containsKey("includeMenu")&&config.getBoolean("includeMenu")){
            return loadMenuOptions(getMenus(false));
        }
        return Collections.emptyList();
    }
    private List<SysWebappMenu> loadMenuOptions(List<? extends SysWebappMenu> menus){
        return menus.stream()
                .map(sysWebappMenuBO -> {
                    if(StringUtils.startsWith(sysWebappMenuBO.getUrl(),"/apps/q")){
                        return getDetail(sysWebappMenuBO.getMenuId());
                    }
                    if(sysWebappMenuBO instanceof WebMenuBO && !CollectionUtils.isEmpty(((WebMenuBO) sysWebappMenuBO).getChildren())){
                        ((WebMenuBO) sysWebappMenuBO).setChildren(loadMenuOptions(((WebMenuBO) sysWebappMenuBO).getChildren()));
                    }
                    return sysWebappMenuBO;
                }).collect(Collectors.toList());
    }

    @Override
    public void deployImport(Object data) {
        if(data!=null)
            doUpdate(((JSONArray)data).toJavaList(WebMenuBO.class));
    }
}
