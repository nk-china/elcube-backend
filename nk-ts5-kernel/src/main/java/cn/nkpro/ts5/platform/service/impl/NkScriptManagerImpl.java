package cn.nkpro.ts5.platform.service.impl;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.basic.NkProperties;
import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.co.*;
import cn.nkpro.ts5.platform.service.NkScriptManager;
import cn.nkpro.ts5.data.mybatis.pagination.PaginationContext;
import cn.nkpro.ts5.data.redis.RedisSupport;
import cn.nkpro.ts5.exception.NkDefineException;
import cn.nkpro.ts5.platform.gen.*;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by bean on 2020/7/17.
 */
@Slf4j
@Service
public class NkScriptManagerImpl implements NkScriptManager {


    @Autowired@SuppressWarnings("all")
    private RedisSupport<List<PlatformScriptWithBLOBs>> redisSupport;
    @Autowired@SuppressWarnings("all")
    private PlatformScriptMapper scriptDefHMapper;
    @Autowired@SuppressWarnings("all")
    private NkCustomObjectManager customObjectManager;
    @Autowired@SuppressWarnings("all")
    private DebugContextManager debugContextManager;
    @Autowired
    private NkProperties nkProperties;

    @Override
    public PageList<PlatformScript> getPage(String keyword,
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

        if(StringUtils.isNotBlank(version)){
            example.getOredCriteria().forEach(
                    criteria -> criteria.andVersionLike(String.format("%%%s",version))
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
        scriptDefH.setVersion(UUID.randomUUID().toString());
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

    @Override
    @Transactional
    public PlatformScript doActive(NkScriptV scriptDefH, boolean force){

        if(!force){
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
        doUpdate(scriptDefH,force);

        Assert.isTrue(!nkProperties.isComponentReloadClassPath(),"IDE模式不能激活脚本");

        debugContextManager.addActiveResource("#"+scriptDefH.getScriptName(), scriptDefH, true);

        redisSupport.delete(Constants.CACHE_DEF_SCRIPT);

        return scriptDefH;
    }

    @Override
    @Transactional
    public void doDelete(NkScriptV scriptDefH){
        Assert.isTrue(StringUtils.equals(scriptDefH.getState(),"InActive"),"非未激活的版本不能删除");
        scriptDefHMapper.deleteByPrimaryKey(scriptDefH);
        debugContextManager.removeDebugResource("#"+scriptDefH.getScriptName(),scriptDefH);
    }

//    /**
//     * 获取同一个Major版本的最后一次更新
//     * @param scriptName scriptName
//     * @param versionPrefix versionPrefix
//     * @return ScriptDefH
//     */
//    private PlatformScript getLastUpdatedVersion(String scriptName,String versionPrefix){
//
//        String major = StringUtils.isBlank(versionPrefix)?"%":(versionPrefix + ".%");
//
//        PlatformScriptExample example = new PlatformScriptExample();
//        example.createCriteria()
//                .andScriptNameEqualTo(scriptName)
//                .andVersionLike(major);
//        example.setOrderByClause("VERSION desc");
//
//        return scriptDefHMapper.selectByExample(example, new RowBounds(0, 1))
//                .stream()
//                .findFirst()
//                .orElse(null);
//    }










//
//    @Override
//    public DefScript getScriptByName(String scriptName) {
//        DefScriptExample example = new DefScriptExample();
//        example.createCriteria().andScriptNameEqualTo(scriptName);
//        return scriptMapper.selectByExampleWithBLOBs(example)
//                .stream()
//                .findFirst().orElse(null);
//    }
//
//    @Override
//    public DefScript update(DefScript script) {
//
//        try {
//
//            ScriptDefinition definition = compileGroovy(script);
//
//            DefScriptExample example = new DefScriptExample();
//            example.createCriteria()
//                    .andScriptNameEqualTo(script.getScriptName());
//
//            scriptMapper.selectByExample(example)
//                .stream()
//                .findAny()
//                .ifPresent(find->{
//                    if(!StringUtils.equals(find.getScriptId(),script.getScriptId())){
//                        throw new TfmsIllegalContentException(String.format("%s 已经存在",script.getScriptName()));
//                    }
//                });
//
//            script.setUpdatedTime(DateTimeUtilz.nowSeconds());
//            if(StringUtils.isBlank(script.getScriptId())){
//                script.setScriptId(guid.nextId(DefScript.class));
//                scriptMapper.insertSelective(script);
//            }else{
//                scriptMapper.updateByPrimaryKeyWithBLOBs(script);
//            }
//
//            LocalSyncUtilz.runAfterCommit(()-> registerBean(definition));
//
//        } catch (ScriptException e) {
//            throw new TfmsIllegalContentException(e.getMessage(),e);
//        }
//
//        return script;
//    }
//
//    @Override
//    public String getClassName(String beanName) {
//        if(beanFactory.containsBean(beanName)){
//            Object bean = beanFactory.getBean(beanName);
//            if(bean instanceof GroovyObject) {
//                return bean.getClass().getName();
//            }
//            return "0";
//        }
//        return null;
//    }


//    @Override
//    public int deployOrder() {
//        return Integer.MIN_VALUE;
//    }
//
//    @Override
//    public Object deployExport(JSONObject config) {
//        if(config.containsKey("includeScript")&&config.getBoolean("includeScript")) {
//            return getAll()
//                    .stream()
//                    .map(defScript -> getScript(defScript.getScriptId()))
//                    .collect(Collectors.toList());
//        }
//        return Collections.emptyList();
//    }
//
//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
//        if(contextRefreshedEvent.getApplicationContext()==beanFactory){
//            try{
//                for(DefScript defScript : scriptMapper.selectByExampleWithBLOBs(null)){
//                    registerBean(compileGroovy(defScript));
//                }
//            }catch (ScriptException e){
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    @Override
//    public void deployImport(Object data) {
//        if(data!=null)
//            ((JSONArray)data).toJavaList(DefScript.class)
//                .forEach(this::update);
//    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(contextRefreshedEvent.getApplicationContext() == debugContextManager.getApplicationContext()){
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
}
