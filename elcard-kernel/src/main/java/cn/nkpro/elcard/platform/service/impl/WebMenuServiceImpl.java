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

import cn.nkpro.elcard.basic.Constants;
import cn.nkpro.elcard.platform.gen.PlatformMenu;
import cn.nkpro.elcard.platform.gen.PlatformMenuExample;
import cn.nkpro.elcard.platform.gen.PlatformMenuMapper;
import cn.nkpro.elcard.platform.model.WebMenuBO;
import cn.nkpro.elcard.data.redis.RedisSupport;
import cn.nkpro.elcard.security.SecurityUtilz;
import cn.nkpro.elcard.platform.DeployAble;
import cn.nkpro.elcard.platform.service.WebMenuService;
import cn.nkpro.elcard.utils.BeanUtilz;
import cn.nkpro.elcard.utils.DateTimeUtilz;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/1/3.
 */
@Order(10)
@Slf4j
@Service
public class WebMenuServiceImpl implements WebMenuService, DeployAble,InitializingBean {

    @Autowired
    private PlatformMenuMapper sysWebappMenuMapper;

    @Autowired
    private RedisSupport<List<WebMenuBO>> redisSupport;

    /**
     * 根据当前用户的权限获取对应的菜单
     * @return
     */
    @Override
    public List<WebMenuBO> getMenus(boolean filterAuth){

        List<WebMenuBO> menus = redisSupport.getIfAbsent(Constants.CACHE_NAV_MENUS,()-> {

            PlatformMenuExample example = new PlatformMenuExample();
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
    public  PlatformMenu getDetail(String id){
        return sysWebappMenuMapper.selectByPrimaryKey(id);
    }

    @Transactional
    @Override
    public void doUpdate(List<WebMenuBO> menus){
        Long updateTime = DateTimeUtilz.nowSeconds();
        menus.forEach(menu->{
            menu.setParentId(null);
            menu.setOrderBy((menus.indexOf(menu)+1) * 10000);
            menu.setMenuId(StringUtils.defaultIfBlank(menu.getMenuId(), UUID.randomUUID().toString()));
            update(menu,updateTime);
            if(menu.getChildren()!=null){
                    menu.getChildren().forEach(m->{
                        m.setParentId(menu.getMenuId());
                        m.setMenuId(StringUtils.defaultIfBlank(m.getMenuId(), UUID.randomUUID().toString()));
                        m.setOrderBy(menu.getOrderBy()+menu.getChildren().indexOf(m));
                        update(m,updateTime);
                    });
            }
        });

        PlatformMenuExample example = new PlatformMenuExample();
        example.createCriteria().andUpdatedTimeLessThan(updateTime);
        sysWebappMenuMapper.deleteByExample(example);

        redisSupport.delete(Constants.CACHE_NAV_MENUS);
    }

    private void update(PlatformMenu menu,Long updateTime){
        menu.setUpdatedTime(updateTime);
        if(sysWebappMenuMapper.selectByPrimaryKey(menu.getMenuId())==null){
            sysWebappMenuMapper.insert(menu);
        }else if(menu.getMenuOptions()==null){
            sysWebappMenuMapper.updateByPrimaryKey(menu);
        }else{
            sysWebappMenuMapper.updateByPrimaryKeyWithBLOBs(menu);
        }
    }

    @Override
    public void afterPropertiesSet() {
        redisSupport.delete(Constants.CACHE_NAV_MENUS);
    }

    @Override
    public void loadExport(JSONArray exports) {
        JSONObject export = new JSONObject();
        export.put("key","includeMenu");
        export.put("name","主菜单");
        exports.add(export);
    }

    @Override
    public void exportConfig(JSONObject config, JSONObject export) {
        if(config.getBooleanValue("includeMenu")){
            export.put("menus",getMenus(false));
        }
    }

    @Override
    public void importConfig(JSONObject data) {
        if(data.containsKey("menus")){
            doUpdate(data.getJSONArray("menus").toJavaList(WebMenuBO.class));
        }
    }
}
