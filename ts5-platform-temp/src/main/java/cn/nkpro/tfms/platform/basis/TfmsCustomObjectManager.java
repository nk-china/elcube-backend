package cn.nkpro.tfms.platform.basis;

import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.custom.TfmsComponent;
import cn.nkpro.tfms.platform.custom.doc.TfmsDocProcessor;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/7/24.
 */
@Component
public class TfmsCustomObjectManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public List<TfmsComponent> getCustomComponents(EnumDocClassify docClassify){
        return applicationContext.getBeansOfType(TfmsComponent.class)
                .values()
                .stream()
                .filter(tfmsComponent -> !tfmsComponent.deprecated())
                .filter(tfmsComponent -> tfmsComponent.supports(docClassify))
                .sorted(Comparator.comparing(TfmsComponent::getComponentName))
                .collect(Collectors.toList());
    }

    public List<TfmsDocProcessor> getDocProcessorNames(EnumDocClassify docClassify){
        return applicationContext.getBeansOfType(TfmsDocProcessor.class)
                .values()
                .stream()
                .filter(processor->processor.classify()==docClassify)
                .sorted(Comparator.comparing(TfmsDocProcessor::getProcessorName))
                .collect(Collectors.toList());
    }

    public List<TfmsCustomObjectDesc> getInterceptorNames(Class<? extends TfmsCustomObject> clazz,boolean emptyValue){
        List<TfmsCustomObjectDesc> list = applicationContext.getBeansOfType(clazz)
                .keySet()
                .stream()
                .sorted()
                .map(name->new TfmsCustomObjectDesc(name,name))
                .collect(Collectors.toList());
        if(emptyValue)
            list.add(0,new TfmsCustomObjectDesc(StringUtils.EMPTY,"空配置"));
        return list;
    }

    public <T extends TfmsCustomObject> T getCustomObjectIfExists(String beanName, Class<T> clazz){
        if(StringUtils.isNotBlank(beanName) && applicationContext.containsBean(beanName)){
            return applicationContext.getBean(beanName,clazz);
        }
        return null;
    }

    public <T extends TfmsCustomObject> T getCustomObject(String beanName, Class<T> clazz){
        Assert.isTrue(applicationContext.containsBean(beanName),String.format("自定义对象[%s]不存在",beanName));
        return applicationContext.getBean(beanName,clazz);
    }

    public <T extends TfmsCustomObject> Map<String,T> getCustomObjects(Class<T> clazz){
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
