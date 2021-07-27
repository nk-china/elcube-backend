package cn.nkpro.ts5.engine.script;

import cn.nkpro.ts5.config.global.NKProperties;
import cn.nkpro.ts5.exception.TfmsException;
import cn.nkpro.ts5.utils.ResourceUtils;
import groovy.lang.GroovyObject;
import lombok.SneakyThrows;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class NKScriptEngine implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    @Autowired@SuppressWarnings("all")
    private NKProperties properties;

    private ApplicationContext applicationContext;

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    public Map<String, String> getVueFromClasspath() {

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

//    public void registerResourcesDefined() throws Exception {
//
//        Resource[] resources = resourcePatternResolver.getResources("classpath*:/nk/cards/*/*.groovy");
//
//        for(Resource resource : resources){
//
//            String packageName = getPackageName(resource);
//
//            Resource[] componentResources = resourcePatternResolver.getResources("classpath*:/nk/cards/"+packageName+"/**");
//
//            registerComponent(packageName,componentResources);
//
//            Arrays.stream(componentResources)
//                    .forEach(System.out::println);
//
////            String groovyCode = ResourceUtils.readText(resource);
////
////            System.out.println(groovyCode);
//
//            //todo 跟DB数据进行对比，如DB不存在则写入DB
//        }
//    }

//    private void registerComponent(String componentKey, Resource[] resources){
//
//        Optional<DefComponentH> byId = componentHRepository.findById(componentKey);
//        if(byId.isPresent()){
//            if(byId.get().getModifyTime()!=null){
//                return;
//            }
//        }
//
//        List<Resource> collect = Arrays.stream(resources)
//                .filter(resource ->
//                          Objects.requireNonNull(resource.getFilename()).endsWith(".groovy")
//                        ||resource.getFilename().endsWith(".vue"))
//                .collect(Collectors.toList());
//        for(Resource resource : collect){
//
//            String filename = resource.getFilename();
//            assert filename != null;
//
//            String type = filename.substring(  filename.lastIndexOf(".")+1);
//            String code = filename.substring(0,filename.lastIndexOf("."));
//            String content = ResourceUtils.readText(resource);
//
//
//            DefComponentI componentI = new DefComponentI();
//            componentI.setId(componentKey);
//            componentI.setCode(code);
//            componentI.setType(type);
//            componentI.setContent(content);
//            componentI.setActive(1);
//            componentI.setCreateTime(new Timestamp(System.currentTimeMillis()));
//            componentI.setModifyTime(componentI.getCreateTime());
//
//            componentIRepository.save(componentI);
//
//
////            DefComponentIPK componentIPK = new DefComponentIPK();
////            componentIPK.setId(componentKey);
////            componentIPK.setKey(key);
////
////            Optional<DefComponentI> byIdI = componentIRepository.findById(componentIPK);
////            if(byIdI.isPresent()){
////            }
//        }
//    }

    private String getPackageName(Resource resource) throws IOException {
        String[] split = resource.getURL().toString().split("[/]");
        return split[split.length-2];
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
    public void onApplicationEvent(ContextRefreshedEvent contextStartedEvent) {
        //this.registerResourcesDefined();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
