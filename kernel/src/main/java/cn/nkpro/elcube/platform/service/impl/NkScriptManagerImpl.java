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

import cn.nkpro.elcube.basic.Constants;
import cn.nkpro.elcube.basic.NkProperties;
import cn.nkpro.elcube.basic.PageList;
import cn.nkpro.elcube.co.*;
import cn.nkpro.elcube.data.mybatis.pagination.PaginationContext;
import cn.nkpro.elcube.data.redis.RedisSupport;
import cn.nkpro.elcube.exception.NkDefineException;
import cn.nkpro.elcube.platform.DeployAble;
import cn.nkpro.elcube.platform.gen.*;
import cn.nkpro.elcube.platform.model.NkHeartbeatEvent;
import cn.nkpro.elcube.platform.service.NkScriptManager;
import cn.nkpro.elcube.utils.BeanUtilz;
import cn.nkpro.elcube.utils.DateTimeUtilz;
import cn.nkpro.elcube.utils.TextUtils;
import cn.nkpro.elcube.utils.UUIDHexGenerator;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/7/17.
 * Updated by bean on 2021/11/29.
 */
@Order(30)
@Slf4j
@Service
public class NkScriptManagerImpl implements NkScriptManager, DeployAble {


    @Autowired@SuppressWarnings("all")
    private RedisSupport<List<PlatformScriptWithBLOBs>> redisSupport;
    @Autowired@SuppressWarnings("all")
    private RedisSupport<String> redisSupportString;
    @Autowired@SuppressWarnings("all")
    private PlatformScriptMapper scriptDefHMapper;
    @Autowired@SuppressWarnings("all")
    private NkCustomObjectManager customObjectManager;
    @Autowired@SuppressWarnings("all")
    private DebugContextManager debugContextManager;
    @Autowired@SuppressWarnings("all")
    private NkProperties nkProperties;

    private String scriptRandom;

    @Override
    public PageList<PlatformScript> getPage(String keyword,
                                        String type,
                                        String version,
                                        String state,
                                        int from,
                                        int rows,
                                        String orderField,
                                        String order) {
        PlatformScriptExample example = new PlatformScriptExample();
        example.createCriteria();

        if(StringUtils.isNotBlank(keyword)){
            example.getOredCriteria().forEach(
                criteria -> criteria.andScriptNameLike(String.format("%%%s%%",keyword))
            );
            example.or().andScriptDescLike(String.format("%%%s%%",keyword));
        }

        if(StringUtils.isNotBlank(type)){
            example.getOredCriteria().forEach(
                    criteria -> criteria.andScriptTypeEqualTo(type)
            );
        }

        if(StringUtils.isNotBlank(version)){
            example.getOredCriteria().forEach(
                    criteria -> criteria.andVersionLike(String.format("%%%s%%",version))
            );
        }
        if(StringUtils.isNotBlank(state)){
            example.getOredCriteria().forEach(
                    criteria -> criteria.andStateEqualTo(state)
            );
        }
        if(StringUtils.isNotBlank(orderField)){
            example.setOrderByClause(String.format("%s %s",orderField,order));
        }else{
            example.setOrderByClause("SCRIPT_NAME");
        }
        PaginationContext context = PaginationContext.init();
        List<PlatformScript> list = scriptDefHMapper.selectByExample(example, new RowBounds(from, rows));
        return new PageList<>(list,from, rows, context.getTotal());
    }

    @Override
    public List<PlatformScriptWithBLOBs> getActiveResources(){
        return redisSupport.getIfAbsent(Constants.CACHE_DEF_SCRIPT,()->{
            PlatformScriptExample example = new PlatformScriptExample();
            example.createCriteria().andStateEqualTo("Active");
            return scriptDefHMapper.selectByExampleWithBLOBs(example);
        });
    }

//    @Override
//    public PlatformScript getLastVersion(String scriptName){
//
//        PlatformScriptExample example = new PlatformScriptExample();
//        example.createCriteria().andScriptNameEqualTo(scriptName);
//        example.setOrderByClause("VERSION desc");
//
//        return scriptDefHMapper.selectByExampleWithBLOBs(example,new RowBounds(0,1))
//                .stream()
//                .findFirst()
//                .orElse(null);
//    }

    @Override
    public PlatformScript getScript(String scriptName,String version) {

        NkCustomObject customObject = customObjectManager.getCustomObjectIfExists(scriptName, NkCustomObject.class);
        if(customObject instanceof NkCustomScriptObject){
            if(StringUtils.equalsAny(version, ((NkCustomScriptObject) customObject).getScriptDef().getVersion())){
                return ((NkCustomScriptObject) customObject).getScriptDef();
            }
        }

        PlatformScriptKey key = new PlatformScriptKey();
        key.setScriptName(scriptName);
        key.setVersion(version);

        return Optional
                .ofNullable(scriptDefHMapper.selectByPrimaryKey(key))
                .orElseThrow(()->new NkDefineException("配置没有找到"));
    }

    @Override
    @Transactional
    public PlatformScript doRun(NkScriptV scriptDefH, boolean run){

        if(run){
            Assert.isTrue(!StringUtils.equals(scriptDefH.getVersion(),"@"),"IDE版本不能调试");
            Assert.isTrue(StringUtils.equals(scriptDefH.getState(),"InActive"),"已激活的版本不能调试");

            doUpdate(scriptDefH,false);
            debugContextManager.addDebugResource("#"+scriptDefH.getScriptName(),scriptDefH);
        }else{
            debugContextManager.removeDebugResource("#"+scriptDefH.getScriptName(),scriptDefH);
        }

        return scriptDefH;
    }

    @Override
    @Transactional
    public PlatformScript doBreach(NkScriptV scriptDefH){

        scriptDefH.setState("InActive");
        scriptDefH.setVersion(UUIDHexGenerator.generate());
        scriptDefH.setCreatedTime(DateTimeUtilz.nowSeconds());
        scriptDefH.setUpdatedTime(DateTimeUtilz.nowSeconds());
        doUpdate(scriptDefH,false);
        return scriptDefH;
    }


    @Override
    @Transactional
    public PlatformScript doUpdate(NkScriptV scriptDefH, boolean force){

        scriptDefH.setState(StringUtils.defaultIfBlank(scriptDefH.getState(),"InActive"));
        scriptDefH.setUpdatedTime(DateTimeUtilz.nowSeconds());
        if(scriptDefHMapper.selectByPrimaryKey(scriptDefH)==null){
            scriptDefHMapper.insertSelective(scriptDefH);
        }else{
            scriptDefHMapper.updateByPrimaryKeySelective(scriptDefH);
        }

        return scriptDefH;
    }

    /**
     * <h4>激活脚本
     * <p>注意：由于系统可能是分布式的，那么一个节点激活脚本后，应把激活的脚本同步到其他节点
     * <p>系统为了避免使用过于复杂的分布式管理工具，采用redis心跳的方式处理脚本的同步，具体步骤如下：
     * <p>step1: 激活脚本时，会重新计算脚本的md5值
     * <p>step2: 激活后更新缓存中的随机值以及激活操作所在节点的scriptRandom属性，因为激活节点的值与redis保持一致，因此不会重复激活
     * <p>step3: 每个节点都会有心跳检测随机值变化，一旦随机值变化，就重新加载脚本
     * <p>step4: 根据md5值的变化，编译新的脚本编译对象并注入到Spring容器
     * <p>
     * <p>随机值有两份 1、本类中的scriptRandom属性，2、缓存中的CACHE_DEF_SCRIPT_RANDOM
     * <p>在 onApplicationEvent 方法中判断两个值是否一致，如果不一致，会根据md5值重新加载脚本
     * @see #scriptRandom
     * @see Constants#CACHE_DEF_SCRIPT_RANDOM
     * @see #onApplicationEvent(ApplicationEvent)
     */
    @Override
    @Transactional
    public PlatformScript doActive(NkScriptV scriptDefH, boolean force){

        if(!force){
            Assert.isTrue(!nkProperties.isComponentReloadClassPath(),"IDE模式不能激活脚本");
            Assert.isTrue(!StringUtils.equals(scriptDefH.getVersion(),"@"),"IDE版本不能激活");
            Assert.isTrue(!StringUtils.equals(scriptDefH.getState(),"Active"),"已激活的版本不能激活");
        }

        // 清理已激活版本
        PlatformScriptWithBLOBs record = new PlatformScriptWithBLOBs();
        record.setState("History");
        PlatformScriptExample example = new PlatformScriptExample();
        example.createCriteria()
                .andScriptNameEqualTo(scriptDefH.getScriptName())
                .andStateEqualTo("Active");
        scriptDefHMapper.updateByExampleSelective(record, example);

        // 激活版本
        scriptDefH.setState("Active");


        // 计算Groovy脚本的md5值
        StringBuilder groovy = new StringBuilder();
        if(StringUtils.isNotBlank(scriptDefH.getGroovyMain()))
            groovy.append(scriptDefH.getGroovyMain());
        if(StringUtils.isNotBlank(scriptDefH.getGroovyRefs()))
            groovy.append(scriptDefH.getGroovyRefs());

        scriptDefH.setGroovyMd5(TextUtils.md5(groovy.toString()));

        // 持久化脚本
        doUpdate(scriptDefH,force);

        debugContextManager.addActiveResource("#"+scriptDefH.getScriptName(), scriptDefH, true);

        // 激活脚本后，更新随机值，并清空脚本缓存

        this.scriptRandom = UUIDHexGenerator.generate();
        redisSupportString.set(Constants.CACHE_DEF_SCRIPT_RANDOM,this.scriptRandom);
        redisSupportString.delete(Constants.CACHE_DEF_SCRIPT);

        return scriptDefH;
    }

    @Override
    @Transactional
    public void doDelete(NkScriptV scriptDefH){
        Assert.isTrue(StringUtils.equals(scriptDefH.getState(),"InActive"),"非未激活的版本不能删除");
        scriptDefHMapper.deleteByPrimaryKey(scriptDefH);
        debugContextManager.removeDebugResource("#"+scriptDefH.getScriptName(),scriptDefH);
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(@NotNull ApplicationEvent event) {

        if(event instanceof ContextRefreshedEvent){
            if(((ContextRefreshedEvent)event).getApplicationContext() == debugContextManager.getApplicationContext()){
                getActiveResources().forEach((scriptDef)-> {
                    try {
                        debugContextManager.addActiveResource(
                                "#"+scriptDef.getScriptName(),
                                BeanUtilz.copyFromObject(scriptDef, NkScriptV.class),
                                !nkProperties.isComponentReloadClassPath());
                    }catch (RuntimeException e){
                        log.error(e.getMessage(),e);
                    }
                });
            }
        }
        if(event instanceof NkHeartbeatEvent && !nkProperties.isComponentReloadClassPath()){
            // 系统心跳事件
            // 每次心跳都检查一下脚本的随机值
            // 如果随机值发生改变，则重新编译脚本对象
            String scriptRandom = redisSupportString.get(Constants.CACHE_DEF_SCRIPT_RANDOM);

            if(!StringUtils.equals(this.scriptRandom,scriptRandom)){

                getActiveResources().forEach(scriptDef->{

                    NkCustomScriptObject object = customObjectManager.getCustomObject(scriptDef.getScriptName(), NkCustomScriptObject.class);

                    String nMd5 = scriptDef.getGroovyMd5();
                    String oMd5 = object.getScriptDef().getGroovyMd5();

                    if(!StringUtils.equals(nMd5, oMd5)){
                        log.info("脚本 {} 发生改变, 立即重新载入", scriptDef.getScriptName());

                        try {
                            debugContextManager.addActiveResource(
                                    "#"+scriptDef.getScriptName(),
                                    BeanUtilz.copyFromObject(scriptDef, NkScriptV.class),
                                    true);
                        }catch (RuntimeException e){
                            log.error(e.getMessage(),e);
                        }
                    }
                });

                this.scriptRandom = scriptRandom;
            }
        }
    }

    @Override
    public void loadExport(JSONArray exports) {

        JSONObject export = new JSONObject();
        export.put("key","scripts");
        export.put("name","组件对象");
        export.put("list",customObjectManager
                .getCustomObjectDescriptionList(NkCustomScriptObject.class,false,(entry)->{
                    NkCustomScriptObject value = (NkCustomScriptObject) entry.getValue();
                    return value.getScriptDef() != null && !StringUtils.equals(value.getScriptDef().getState(), "Native");
                }));
        exports.add(export);
    }

    @Override
    public void exportConfig(JSONObject config, JSONObject export) {

        if(config.getJSONArray("scripts")!=null){
            export.put("scripts",
                    config.getJSONArray("scripts").stream().map(beanName->
                            customObjectManager.getCustomObject((String) beanName, NkCustomScriptObject.class).getScriptDef()
                    ).collect(Collectors.toList())
            );
        }
    }

    @Override
    public void importConfig(JSONObject data) {

        if(data.containsKey("scripts")){
            data.getJSONArray("scripts").toJavaList(NkScriptV.class)
                    .forEach(scriptV -> doActive(scriptV, true));
        }
    }
}
