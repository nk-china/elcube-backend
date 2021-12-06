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
package cn.nkpro.elcard.platform.controller;

import cn.nkpro.elcard.annotation.Keep;
import cn.nkpro.elcard.annotation.NkNote;
import cn.nkpro.elcard.co.*;
import cn.nkpro.elcard.exception.NkSystemException;
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
public class ScriptResController {

    @Autowired@SuppressWarnings("all")
    private NkCustomObjectManager customObjectManager;

    @NkNote("1.获取卡片信息")
    @RequestMapping("/vue")
    public Map<Object, Object> vueTemplates() {

        Map<Object, Object> vueMap = new HashMap<>();
        customObjectManager.getCustomObjects(NkScriptComponent.class)
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
            NkScriptV scriptDef = ((NkCustomScriptObject) customObject).getScriptDef();
            if(scriptDef!=null){
                return new BeanDescribe(
                        scriptDef.getScriptName(),
                        customObject instanceof GroovyObject,
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

    @Keep
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
