package cn.nkpro.tfms.platform.services.impl;

import cn.nkpro.tfms.platform.basis.TfmsCustomObject;
import cn.nkpro.ts5.exception.TfmsIllegalContentException;
import cn.nkpro.tfms.platform.mappers.gen.DefScriptMapper;
import cn.nkpro.tfms.platform.model.po.DefScript;
import cn.nkpro.tfms.platform.model.po.DefScriptExample;
import cn.nkpro.tfms.platform.services.TfmsDefDeployAble;
import cn.nkpro.tfms.platform.services.TfmsDefScriptService;
import cn.nkpro.ts5.supports.GUID;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import cn.nkpro.ts5.utils.SpringEmulated;
import cn.nkpro.ts5.utils.LocalSyncUtilz;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import groovy.lang.GroovyObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/7/17.
 */
@Order()
@Service
public class TfmsDefScriptServiceImpl implements TfmsDefScriptService, TfmsDefDeployAble, ApplicationContextAware, InitializingBean, ApplicationListener<ContextRefreshedEvent> {

    private ScriptEngineManager manager = new ScriptEngineManager();
    private ScriptEngine engine = manager.getEngineByName("groovy");
    private BeanDefinitionRegistry beanDefinitionRegistry;
    private ApplicationContext beanFactory;

    @Autowired
    private GUID guid;
    @Autowired
    private DefScriptMapper scriptMapper;

    @Override
    public DefScript getScript(String scriptId) {
        return scriptMapper.selectByPrimaryKey(scriptId);
    }

    @Override
    public DefScript getScriptByName(String scriptName) {
        DefScriptExample example = new DefScriptExample();
        example.createCriteria().andScriptNameEqualTo(scriptName);
        return scriptMapper.selectByExampleWithBLOBs(example)
                .stream()
                .findFirst().orElse(null);
    }

    @Override
    public DefScript update(DefScript script) {

        try {

            ScriptDefinition definition = compileGroovy(script);

            DefScriptExample example = new DefScriptExample();
            example.createCriteria()
                    .andScriptNameEqualTo(script.getScriptName());

            scriptMapper.selectByExample(example)
                .stream()
                .findAny()
                .ifPresent(find->{
                    if(!StringUtils.equals(find.getScriptId(),script.getScriptId())){
                        throw new TfmsIllegalContentException(String.format("%s 已经存在",script.getScriptName()));
                    }
                });

            script.setUpdatedTime(DateTimeUtilz.nowSeconds());
            if(StringUtils.isBlank(script.getScriptId())){
                script.setScriptId(guid.nextId(DefScript.class));
                scriptMapper.insertSelective(script);
            }else{
                scriptMapper.updateByPrimaryKeyWithBLOBs(script);
            }

            LocalSyncUtilz.runAfterCommit(()-> registerBean(definition));

        } catch (ScriptException e) {
            throw new TfmsIllegalContentException(e.getMessage(),e);
        }

        return script;
    }

    @Override
    public List<DefScript> getAll() {
        DefScriptExample example = new DefScriptExample();
        example.setOrderByClause("SCRIPT_ID");
        return scriptMapper.selectByExample(example);
    }

    @Override
    public String getClassName(String beanName) {
        if(beanFactory.containsBean(beanName)){
            Object bean = beanFactory.getBean(beanName);
            if(bean instanceof GroovyObject) {
                return bean.getClass().getName();
            }
            return "0";
        }
        return null;
    }

    private ScriptDefinition compileGroovy(DefScript script) throws ScriptException {

        Class<?> clazz = null;
        try{
            clazz = (Class) engine.eval(script.getScriptContent());
        }catch (ScriptException e){
            throw new RuntimeException(
                    String.format("编译Groovy对象 [%s] 发生错误%s\n%s",
                            script.getScriptName(),
                            script.getScriptContent(),
                            e.getMessage()));
        }

        String beanName = SpringEmulated.decapitalize(clazz.getSimpleName());

        Component component = clazz.getDeclaredAnnotation(Component.class);
        if(component!=null && StringUtils.isNotBlank(component.value())){
            beanName = component.value();
        }
        Service service = clazz.getDeclaredAnnotation(Service.class);
        if(service!=null && StringUtils.isNotBlank(service.value())){
            beanName = service.value();
        }

        // 避免非法重写spring的类
        if(beanFactory.containsBean(beanName)){

            Object exists = beanFactory.getBean(beanName);

            if(!(exists instanceof GroovyObject)){
                if(exists instanceof TfmsCustomObject){
                    if(((TfmsCustomObject) exists).isFinal()){
                        throw new RuntimeException(String.format("%s 不支持重写",exists.getClass().getName()));
                    }
                }else{
                    throw new RuntimeException(String.format("%s 不支持重写",exists.getClass().getName()));
                }
            }
        }
        script.setScriptName(clazz.getName());

        ScriptDefinition definition = new ScriptDefinition();
        definition.beanName = beanName;
        definition.clazz = clazz;
        return definition;
    }

    private void registerBean(ScriptDefinition definition){

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(definition.clazz);
        beanDefinitionRegistry.registerBeanDefinition(definition.beanName,beanDefinitionBuilder.getBeanDefinition());
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = applicationContext;
        this.beanDefinitionRegistry = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public int deployOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public Object deployExport(JSONObject config) {
        if(config.containsKey("includeScript")&&config.getBoolean("includeScript")) {
            return getAll()
                    .stream()
                    .map(defScript -> getScript(defScript.getScriptId()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(contextRefreshedEvent.getApplicationContext()==beanFactory){
            try{
                for(DefScript defScript : scriptMapper.selectByExampleWithBLOBs(null)){
                    registerBean(compileGroovy(defScript));
                }
            }catch (ScriptException e){
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void deployImport(Object data) {
        if(data!=null)
            ((JSONArray)data).toJavaList(DefScript.class)
                .forEach(this::update);
    }

    private static class ScriptDefinition{
        private String beanName;
        private Class<?> clazz;
    }
}
