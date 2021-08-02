package cn.nkpro.ts5.engine.co;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.config.global.NkProperties;
import cn.nkpro.ts5.engine.doc.NkCard;
import cn.nkpro.ts5.engine.doc.model.ScriptDefHV;
import cn.nkpro.ts5.utils.ClassUtils;
import cn.nkpro.ts5.utils.GroovyUtils;
import cn.nkpro.ts5.utils.ResourceUtils;
import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class NkAbstractCustomScriptObject implements NkCustomScriptObject, InitializingBean {

    @Autowired
    private NkProperties properties;

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @Getter
    protected String beanName;

    @Setter@Getter
    protected ScriptDefHV scriptDef;

    public NkAbstractCustomScriptObject() {
        this.beanName = parseComponentName();
    }

    public final void afterPropertiesSet(){
        if(this.scriptDef==null){
            // 从classpath中加载资源
            this.scriptDef = loadScriptFromClassPath();
        }
        if(this.scriptDef==null){
            scriptDef = new ScriptDefHV();
            scriptDef.setScriptType("Unknown");
            scriptDef.setScriptName(beanName);
            scriptDef.setVersion("@");
            scriptDef.setGroovyMain(null);
            scriptDef.setVueMain(null);
            scriptDef.setVueDefs(null);
            scriptDef.setState("Active");
        }
    }

    protected ScriptDefHV loadScriptFromClassPath() {

        String className = getClass().getSimpleName();

        List<String> groovyCode = findResource(className + ".groovy");
        if (!groovyCode.isEmpty()) {
            List<String> vueMainCode = findResource(className + ".vue");
            List<String> vueDefsCode = findResource(className + "Def*.vue");
            ScriptDefHV scriptDefH = new ScriptDefHV();
            scriptDefH.setScriptName(beanName);
            scriptDefH.setVersion("@");
            scriptDefH.setGroovyMain(groovyCode.stream().findFirst().orElse(null));
            scriptDefH.setVueMain(vueMainCode.stream().findFirst().orElse(null));
            scriptDefH.setVueDefs(JSON.toJSONString(vueDefsCode));
            scriptDefH.setState("Active");

            Class<?> groovy = GroovyUtils.compileGroovy(className, scriptDefH.getGroovyMain());
            List interfaces = org.apache.commons.lang.ClassUtils.getAllInterfaces(groovy);

            scriptDefH.setScriptType(interfaces.contains(NkCard.class) ? "Card" : "Service");

            WsDocNote annotation = groovy.getAnnotation(WsDocNote.class);
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
