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
package cn.nkpro.elcube.co;

import cn.nkpro.elcube.annotation.NkNote;
import cn.nkpro.elcube.annotation.NkScriptType;
import cn.nkpro.elcube.basic.NkProperties;
import cn.nkpro.elcube.exception.NkDefineException;
import cn.nkpro.elcube.utils.ClassUtils;
import cn.nkpro.elcube.utils.GroovyUtils;
import cn.nkpro.elcube.utils.ResourceUtils;
import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 抽象的脚本对象基础类
 *
 * 由它来管理脚本对象的脚本内容
 */
public abstract class NkAbstractCustomScriptObject implements NkCustomScriptObject, InitializingBean {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private NkProperties properties;

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @Getter
    protected String beanName;

    @Setter@Getter
    protected NkScriptV scriptDef;

    public NkAbstractCustomScriptObject() {
        this.beanName = parseComponentName();
    }

    public final void afterPropertiesSet(){
        if(this.scriptDef==null){
            // 从classpath中加载资源
            this.scriptDef = loadScriptFromClassPath();
        }
        if(this.scriptDef==null){
            scriptDef = new NkScriptV();
            scriptDef.setScriptType("Unknown");
            scriptDef.setScriptName(beanName);
            scriptDef.setVersion("@");
            scriptDef.setGroovyMain(null);
            scriptDef.setVueMain(null);
            scriptDef.setVueDefs(null);
            scriptDef.setState("Native");
        }
    }


    protected NkScriptV scriptDefHV(){
        if(!scriptDef.isDebug() && properties.isComponentReloadClassPath()){
            NkScriptV defHV = loadScriptFromClassPath();
            if(defHV!=null){
                return defHV;
            }
        }
        return this.scriptDef;
    }

    private NkScriptV loadScriptFromClassPath() {

        String className = getClass().getSimpleName();

        List<String> groovyCode = findResource(className + ".groovy");
        if (!groovyCode.isEmpty()) {
            List<String> vueMainCode = findResource(className + ".vue");
            List<String> vueDefsCode = findResource(className + "Def*.vue");
            NkScriptV scriptDefH = new NkScriptV();
            scriptDefH.setScriptName(beanName);
            scriptDefH.setVersion("@");
            scriptDefH.setGroovyMain(groovyCode.stream().findFirst().orElse(null));
            scriptDefH.setVueMain(vueMainCode.stream().findFirst().orElse(null));
            scriptDefH.setVueDefs(CollectionUtils.isEmpty(vueDefsCode)?null:JSON.toJSONString(vueDefsCode));
            scriptDefH.setState("Active");

            Class<?> groovy = GroovyUtils.compileGroovy(className, scriptDefH.getGroovyMain());
            List<Class<?>> interfaces = org.apache.commons.lang3.ClassUtils.getAllInterfaces(groovy);

            if(interfaces.contains(NkCustomScriptObject.class)){
                scriptDefH.setScriptType(
                    interfaces.stream()
                            .map(i -> i.getAnnotation(NkScriptType.class))
                            .filter(Objects::nonNull)
                            .findFirst()
                            .map(NkScriptType::value)
                            .orElse("Service")
                );
            }else{
                throw new NkDefineException("组件必须实现NkScriptObject接口");
            }

            NkNote annotation = groovy.getAnnotation(NkNote.class);
            scriptDefH.setScriptDesc(annotation != null ? annotation.value() : beanName);
            return scriptDefH;
        }
        return null;
    }



    private String parseComponentName(){

        Class<?> clazz = getClass();

        Component component = clazz.getDeclaredAnnotation(Component.class);
        if(component!=null && StringUtils.isNotBlank(component.value()))
            return component.value();

        Service service = clazz.getDeclaredAnnotation(Service.class);
        if(service!=null && StringUtils.isNotBlank(service.value()))
            return service.value();

        return ClassUtils.decapitateClassName(clazz.getSimpleName());
    }

    private List<String> findResource(String resourceName){
        try {
            List<Resource> resources = new ArrayList<>();
            for (String path : properties.getComponentBasePackages()) {
                resources.addAll(Arrays.asList(resourcePatternResolver.getResources(String.format("classpath*:/%s/**/%s", packageToPath(path), resourceName))));
            }
            return resources.stream()
                    .map(ResourceUtils::readText)
                    .collect(Collectors.toList());
        }catch (Exception e){
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private String packageToPath(String packageName){
        packageName = packageName.replaceAll("[.]","/");
        if(packageName.startsWith("/")){
            packageName = packageName.substring(1);
        }
        if(packageName.endsWith("/")){
            packageName = packageName.substring(0,packageName.length()-1);
        }
        return packageName;
    }
}
