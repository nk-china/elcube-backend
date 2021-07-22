package cn.nkpro.ts5.basic;

import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.mvel2.util.Make;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/7/24.
 */
@Component
public class NKCustomObjectManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;

//    public List<NKComponent> getCustomComponents(){
//        return applicationContext.getBeansOfType(NKComponent.class)
//                .values()
//                .stream()
//                .filter(tfmsComponent -> !tfmsComponent.deprecated())
//                .sorted(Comparator.comparing(NKComponent::getComponentName))
//                .collect(Collectors.toList());
//    }
//
//    public List<NKDocProcessor> getDocProcessorNames(){
//        return applicationContext.getBeansOfType(NKDocProcessor.class)
//                .values()
//                .stream()
//                .sorted(Comparator.comparing(NKDocProcessor::getProcessorName))
//                .collect(Collectors.toList());
//    }

    public List<NKCustomObjectDesc> getInterceptorNames(Class<? extends NKCustomObject> clazz, boolean emptyValue){
        List<NKCustomObjectDesc> list = applicationContext.getBeansOfType(clazz)
                .keySet()
                .stream()
                .sorted()
                .map(name->new NKCustomObjectDesc(name,name))
                .collect(Collectors.toList());
        if(emptyValue)
            list.add(0,new NKCustomObjectDesc(StringUtils.EMPTY,"空配置"));
        return list;
    }

    public List<NKCustomObjectDesc> getCustomObjectDescriptionList(Class<? extends NKCustomObject> clazz, boolean emptyValue, Predicate<Map.Entry<String,? extends NKCustomObject>> predicate){
        if(predicate==null){
            predicate = (e)-> true;
        }
        List<NKCustomObjectDesc> list = applicationContext.getBeansOfType(clazz)
                .entrySet()
                .stream()
                .filter(predicate)
                .map((e)->new NKCustomObjectDesc(e.getKey(),e.getValue().desc()))
                .sorted(Comparator.comparing(NKCustomObjectDesc::getName))
                .collect(Collectors.toList());
        if(emptyValue)
            list.add(0,new NKCustomObjectDesc(StringUtils.EMPTY,"空配置"));
        return list;
    }

    public <T extends NKCustomObject> T getCustomObjectIfExists(String beanName, Class<T> clazz){
        if(StringUtils.isNotBlank(beanName) && applicationContext.containsBean(beanName)){
            return applicationContext.getBean(beanName,clazz);
        }
        return null;
    }

    public <T extends NKCustomObject> T getCustomObject(String beanName, Class<T> clazz){
        Assert.isTrue(applicationContext.containsBean(beanName),String.format("自定义对象[%s]不存在",beanName));
        return applicationContext.getBean(beanName,clazz);
    }

    public <T extends NKCustomObject> Map<String,T> getCustomObjects(Class<T> clazz){
         Map<String,T> beans = new HashMap<>();
         Arrays.stream(applicationContext.getBeanNamesForType(clazz))
                .forEach(name-> beans.put(name,applicationContext.getBean(name,clazz)));
         return beans;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
