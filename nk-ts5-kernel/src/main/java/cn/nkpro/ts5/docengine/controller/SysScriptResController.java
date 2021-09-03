package cn.nkpro.ts5.docengine.controller;

import cn.nkpro.ts5.co.NkCustomObject;
import cn.nkpro.ts5.co.NkCustomObjectManager;
import cn.nkpro.ts5.co.NkCustomScriptObject;
import cn.nkpro.ts5.co.ScriptDefHV;
import cn.nkpro.ts5.docengine.NkCard;
import cn.nkpro.ts5.exception.NkSystemException;
import cn.nkpro.ts5.annotation.NkNote;
import groovy.lang.GroovyObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bean on 2020/1/15.
 */
@NkNote("35.[DevDef]脚本资源")
@RestController
@RequestMapping("/def/resources")
public class SysScriptResController {

    @Autowired@SuppressWarnings("all")
    private NkCustomObjectManager customObjectManager;

    @NkNote("1.获取卡片信息")
    @RequestMapping("/vue")
    public Map<Object, Object> vueTemplates() {

        Map<Object, Object> vueMap = new HashMap<>();
        customObjectManager.getCustomObjects(NkCard.class)
                .values()
                .forEach(nkCard -> vueMap.putAll(nkCard.getVueTemplate()));
        return vueMap;
    }

    @NkNote("5.获取脚本的Groovy类名")
    @RequestMapping("/bean/{beanName}")
    public BeanDescribe className(@PathVariable("beanName") String beanName){

        Object customObject = customObjectManager.getCustomObjectIfExists(beanName, NkCustomObject.class);

        if(customObject==null){
            return new BeanDescribe(
                    null,
                    false,
                    "@",
                    "NotFound",
                    false);
        }

        if(customObject instanceof NkCustomScriptObject){
            ScriptDefHV scriptDef = ((NkCustomScriptObject) customObject).getScriptDef();
            if(scriptDef!=null){
                return new BeanDescribe(
                        scriptDef.getScriptName(),
                        true,
                        scriptDef.getVersion(),
                        scriptDef.getState(),
                        scriptDef.isDebug());
            }
        }

        customObject = getTargetBean(customObject);
        return new BeanDescribe(
                customObject.getClass().getSimpleName(),
                customObject instanceof GroovyObject,
                "@",
                "Native",
                false);

    }

    private Object getTargetBean(Object bean){
        if(AopUtils.isAopProxy(bean)){
            try {
                bean = ((Advised)bean).getTargetSource().getTarget();
            } catch (Exception e) {
                throw new NkSystemException(e.getMessage(),e);
            }
        }
        return bean;
    }

    @Data
    @AllArgsConstructor
    public static
    class BeanDescribe{
        String className;
        boolean isGroovy;
        String version;
        String state;
        boolean debug;
    }
}
