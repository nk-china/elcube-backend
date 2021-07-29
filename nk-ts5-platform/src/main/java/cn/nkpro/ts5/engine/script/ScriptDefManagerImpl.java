package cn.nkpro.ts5.engine.script;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.config.mybatis.pagination.PaginationContext;
import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.orm.mb.gen.*;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import cn.nkpro.ts5.utils.VersioningUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by bean on 2020/7/17.
 */
@Service
public class ScriptDefManagerImpl implements ScriptDefManager {


    @Autowired
    private RedisSupport<List<ScriptDefHWithBLOBs>> redisSupport;
    @Autowired
    private ScriptDefHMapper scriptDefHMapper;

    @Override
    public PageList<ScriptDefH> getPage(String keyword,
                                    String version,
                                    String state,
                                    int from,
                                    int rows,
                                    String orderField,
                                    String order) {
        ScriptDefHExample example = new ScriptDefHExample();
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
        List<ScriptDefH> list = scriptDefHMapper.selectByExample(example, new RowBounds(from, rows));
        return new PageList<>(list,from, rows, context.getTotal());
    }

    @Override
    public List<ScriptDefHWithBLOBs> getActiveResources(){
        return redisSupport.getIfAbsent(Constants.CACHE_DEF_SCRIPT,()->{
            ScriptDefHExample example = new ScriptDefHExample();
            example.createCriteria().andStateEqualTo("Active");
            return scriptDefHMapper.selectByExampleWithBLOBs(example);
        });
    }

    @Override
    public ScriptDefHWithBLOBs getLastVersion(String scriptName){

        ScriptDefHExample example = new ScriptDefHExample();
        example.createCriteria().andScriptNameEqualTo(scriptName);
        example.setOrderByClause("VERSION desc");

        return scriptDefHMapper.selectByExampleWithBLOBs(example,new RowBounds(0,1))
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public ScriptDefH getScript(String scriptName,String version) {

        ScriptDefHKey key = new ScriptDefHKey();
        key.setScriptName(scriptName);
        key.setVersion(version);

        return scriptDefHMapper.selectByPrimaryKey(key);
    }

    @Override
    @Transactional
    public ScriptDefH doEdit(ScriptDefHWithBLOBs scriptDefH){
        if(StringUtils.equals(scriptDefH.getState(),"Active")){
            ScriptDefH lastUpdatedVersion = getLastUpdatedVersion(scriptDefH.getScriptName(), VersioningUtils.parseMinor(scriptDefH.getVersion()));
            // 增加Patch
            scriptDefH.setVersion(VersioningUtils.nextPatch(lastUpdatedVersion.getVersion()));
            scriptDefH.setState("InActive");
            return doUpdate(scriptDefH,false);
        }
        return scriptDefH;
    }


    @Override
    @Transactional
    public ScriptDefH doUpdate(ScriptDefHWithBLOBs scriptDefH, boolean force){

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
     * 获取同一个Major版本的最后一次更新
     * @param scriptName scriptName
     * @param versionPrefix versionPrefix
     * @return ScriptDefH
     */
    private ScriptDefH getLastUpdatedVersion(String scriptName,String versionPrefix){

        String major = StringUtils.isBlank(versionPrefix)?"%":(versionPrefix + ".%");

        ScriptDefHExample example = new ScriptDefHExample();
        example.createCriteria()
                .andScriptNameEqualTo(scriptName)
                .andVersionLike(major);
        example.setOrderByClause("VERSION desc");

        return scriptDefHMapper.selectByExample(example, new RowBounds(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
    }










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
}
