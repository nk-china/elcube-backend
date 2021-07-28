package cn.nkpro.ts5.engine.script;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.basic.NKCustomObject;
import cn.nkpro.ts5.config.global.NKProperties;
import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.engine.devops.DebugSupport;
import cn.nkpro.ts5.exception.TfmsException;
import cn.nkpro.ts5.orm.mb.gen.CardDefHKey;
import cn.nkpro.ts5.orm.mb.gen.CardDefHMapper;
import cn.nkpro.ts5.orm.mb.gen.CardDefHWithBLOBs;
import cn.nkpro.ts5.utils.ResourceUtils;
import cn.nkpro.ts5.utils.ClassUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import groovy.lang.GroovyObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NKScriptEngine implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    private ScriptEngineManager manager = new ScriptEngineManager();
    private ScriptEngine engine = manager.getEngineByName("groovy");
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private ApplicationContext applicationContext;
    private BeanDefinitionRegistry beanDefinitionRegistry;

    @Autowired@SuppressWarnings("all")
    private NKProperties properties;
    @Autowired@SuppressWarnings("all")
    private DebugSupport debugSupport;
    @Autowired@SuppressWarnings("all")
    private CardDefHMapper cardDefHMapper;
    @Autowired@SuppressWarnings("all")
    private RedisSupport<CardDefHWithBLOBs> redisSupport;


    public Map<String, String> buildVueMap(){

        // 从本地资源获取
        Map<String, String> vueMap = getVueMapFromClasspath();

        // 从数据库获取
        getResources().forEach((k,v)->{
            if(StringUtils.isNotBlank(v.getVueMain())){
                vueMap.put(k,v.getVueMain());
            }
            if(StringUtils.isNotBlank(v.getVueDefs())){
                JSONArray array = JSON.parseArray(v.getVueDefs());
                array.forEach((item)->{
                    int index = array.indexOf(item);
                    vueMap.put(k+"Def"+(index==0?"":index), (String) item);
                });
            }
        });

        // 从Debug上下文获取
//        if(debugSupport.getDebugId()!=null){
//            // load resource from debug context
//            System.out.println();
//        }

        return vueMap;
    }

    private Map<String, String> getVueMapFromClasspath() {

        List<Resource> resources = new ArrayList<>();

        Arrays.stream(properties.getVueBasePackages())
                .forEach(path->{
                    try {
                        path = path.replaceAll("[.]","/");
                        resources.addAll(Arrays.asList(resourcePatternResolver.getResources("classpath*:/"+path+"/**/*.vue")));
                    } catch (IOException ignored) {
                    }
                });

        return resources.stream()
                .collect(Collectors.toMap(
                        resource -> Objects.requireNonNull(resource.getFilename()).substring(0,resource.getFilename().length()-4),
                        ResourceUtils::readText
                ));
    }

    public void registerGroovyObject(String groovyName, String groovyCode){

        Class<?> clazz;

        try {
            clazz = (Class<?>) engine.eval(groovyCode);
        } catch (ScriptException e) {
            throw new RuntimeException(
                    String.format("编译Groovy对象 [%s] 发生错误: %s",
                            groovyName,
                            e.getMessage()),e);
        }

        String beanName = ClassUtils.decapitateClassName(clazz.getSimpleName());

        Component component = clazz.getDeclaredAnnotation(Component.class);
        if(component!=null && StringUtils.isNotBlank(component.value())){
            beanName = component.value();
        }
        Service service = clazz.getDeclaredAnnotation(Service.class);
        if(service!=null && StringUtils.isNotBlank(service.value())){
            beanName = service.value();
        }


        // 避免非法重写spring的类
        if(applicationContext.containsBean(beanName)){

            Object exists = applicationContext.getBean(beanName);

            if(!(exists instanceof GroovyObject)){
                if(exists instanceof NKCustomObject){
                    if(((NKCustomObject) exists).isFinal()){
                        throw new RuntimeException(String.format("%s 不支持重写",exists.getClass().getName()));
                    }
                }else{
                    throw new RuntimeException(String.format("%s 不支持重写",exists.getClass().getName()));
                }
            }
        }

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        beanDefinitionRegistry.registerBeanDefinition(beanName,beanDefinitionBuilder.getBeanDefinition());
    }

    private void autoRegisterGroovyObject(){
        getResources().forEach((k,v)-> {
            try {
                registerGroovyObject(k, v.getGroovyMain());
            }catch (RuntimeException e){
                log.error(e.getMessage(),e);
            }
        });
    }

    private Map<String, CardDefHWithBLOBs> getResources(){
        return redisSupport.getHashIfAbsent(Constants.CACHE_DEF_SCRIPT,()->
                cardDefHMapper.selectByExampleWithBLOBs(null)
                        .stream()
                        .collect(Collectors.toMap(CardDefHKey::getComponentName,v->v))
        );
    }

    public String getClassName(String beanName) {
        if(applicationContext.containsBean(beanName)){
            Object bean = applicationContext.getBean(beanName);

            if(AopUtils.isAopProxy(bean)){
                try {
                    bean = ((Advised)bean).getTargetSource().getTarget();
                } catch (Exception e) {
                    throw new TfmsException(e.getMessage(),e);
                }
            }

            if(bean instanceof GroovyObject) {
                return bean.getClass().getName();
            }
            return "0";
        }
        return null;
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
        this.beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
    }
}
