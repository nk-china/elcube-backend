package cn.nkpro.ts5.engine.script;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.devops.DebugSupport;
import cn.nkpro.ts5.engine.doc.NKCard;
import cn.nkpro.ts5.orm.mb.gen.ScriptDefH;
import cn.nkpro.ts5.orm.mb.gen.ScriptDefHWithBLOBs;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class NKScriptEngine implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    private ApplicationContext applicationContext;

    @Autowired
    private ClasspathResourceLoader classpathResourceLoader;
    @Autowired
    private GroovyManager groovyManager;
    @Autowired
    private ScriptDefManager scriptDefManager;
    @Autowired
    private DebugSupport debugSupport;

    public Map<String, String> getRuntimeVueMap(){
        // 从本地资源获取
        Map<String, String> vueMap = classpathResourceLoader.getVueMapFromClasspath();
        // 从数据库获取
        scriptDefManager.getActiveResources().forEach((script)-> putVueToMap(script,vueMap));
        // 从Debug上下文获取
        debugSupport.getDebugObjects("$").forEach(script-> putVueToMap((ScriptDefHWithBLOBs) script,vueMap));
        return vueMap;
    }

    public void setDebugScript(ScriptDefHWithBLOBs scriptDefH){
        debugSupport.setDebugClass(
                groovyManager.compileGroovy(scriptDefH.getScriptName(), scriptDefH.getGroovyMain())
        );
        debugSupport.setDebugResource(String.format("$%s",scriptDefH.getScriptName()),scriptDefH);
    }

    public ScriptDefH getRuntimeScript(String scriptName, String version) {

        if(StringUtils.equals(version,"@")){
            // 查找debug版本
            ScriptDefHWithBLOBs scriptDefH = (ScriptDefHWithBLOBs) debugSupport.getDebugObject(String.format("$%s", scriptName)).orElse(null);
            if(scriptDefH != null)
                return scriptDefH;

            // 查找active版本
            scriptDefH = scriptDefManager.getActiveResources()
                    .stream()
                    .filter(i->StringUtils.equals(i.getScriptName(),scriptName))
                    .findFirst().orElse(null);

            if(scriptDefH != null)
                return scriptDefH;

            // 如果数据库没有，尝试从classpath加载
            List<String> groovyCode =  classpathResourceLoader.findResource(scriptName + ".groovy");
            if(!groovyCode.isEmpty()){
                List<String> vueMainCode = classpathResourceLoader.findResource(scriptName + ".vue");
                List<String> vueDefsCode = classpathResourceLoader.findResource(scriptName + "Def*.vue");
                scriptDefH = new ScriptDefHWithBLOBs();
                scriptDefH.setScriptName(scriptName);
                scriptDefH.setVersion(version);
                scriptDefH.setGroovyMain(groovyCode.stream().findFirst().orElse(null));
                scriptDefH.setVueMain(vueMainCode.stream().findFirst().orElse(null));
                scriptDefH.setVueDefs(JSON.toJSONString(vueDefsCode));

                Class<?> groovy = groovyManager.compileGroovy(scriptName, scriptDefH.getGroovyMain());
                List interfaces = ClassUtils.getAllInterfaces(groovy);

                scriptDefH.setScriptType(interfaces.contains(NKCard.class)?"Card":"Service");

                WsDocNote annotation = groovy.getAnnotation(WsDocNote.class);
                scriptDefH.setScriptDesc(annotation!=null?annotation.value():scriptName);
                return scriptDefH;
            }

            // 都没有找到
            return null;

        }

        return scriptDefManager.getScript(scriptName,version);
    }

    private void autoRegisterGroovyObject(){
        scriptDefManager.getActiveResources().forEach((script)-> {
            try {
                groovyManager.registerGroovyObject(groovyManager.compileGroovy(script.getScriptName(), script.getGroovyMain()));
            }catch (RuntimeException e){
                log.error(e.getMessage(),e);
            }
        });
    }

    private void putVueToMap(ScriptDefHWithBLOBs script,Map<String, String> vueMap){
        if(StringUtils.isNotBlank(script.getVueMain())){
            vueMap.put(script.getScriptName(),script.getVueMain());
        }
        if(StringUtils.isNotBlank(script.getVueDefs())){
            JSONArray array = JSON.parseArray(script.getVueDefs());
            array.forEach((item)->{
                int index = array.indexOf(item);
                vueMap.put(script.getScriptName()+"Def"+(index==0?"":index), (String) item);
            });
        }
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(contextRefreshedEvent.getApplicationContext()==applicationContext){
            this.autoRegisterGroovyObject();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
