package cn.nkpro.ts5.basic;

import cn.nkpro.ts5.engine.devops.DebugSupport;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private DebugSupport debugSupport;

    private ApplicationContext applicationContext;

    public List<NKCustomObjectDesc> getCustomObjectDescriptionList(Class<? extends NKCustomObject> clazz, boolean emptyValue, Predicate<Map.Entry<String,? extends NKCustomObject>> predicate){
        if(predicate==null){
            predicate = (e)-> true;
        }
        List<NKCustomObjectDesc> list = getApplicationContext().getBeansOfType(clazz)
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
        if(StringUtils.isNotBlank(beanName) && getApplicationContext().containsBean(beanName)){
            return getApplicationContext().getBean(beanName,clazz);
        }
        return null;
    }

    public <T extends NKCustomObject> T getCustomObject(String beanName, Class<T> clazz){
        Assert.isTrue(getApplicationContext().containsBean(beanName),String.format("自定义对象[%s]不存在",beanName));
        return getApplicationContext().getBean(beanName,clazz);
    }

    public <T extends NKCustomObject> Map<String,T> getCustomObjects(Class<T> clazz){
         Map<String,T> beans = new HashMap<>();
         Arrays.stream(getApplicationContext().getBeanNamesForType(clazz))
                .forEach(name-> beans.put(name,getApplicationContext().getBean(name,clazz)));
         return beans;
    }

    private ApplicationContext getApplicationContext(){
        return Optional.ofNullable(debugSupport.getDebugApplicationContext())
                .orElse(this.applicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
