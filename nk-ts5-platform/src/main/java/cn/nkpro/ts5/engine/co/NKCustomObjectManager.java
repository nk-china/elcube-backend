package cn.nkpro.ts5.engine.co;

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


    public <T extends NKCustomObject> Map<String,T> getCustomObjects(Class<T> clazz){

        // 注意： 通过type从Spring上下文中获取bean时，只会从当前上下文中查找
        // 所以需要从 applicationContext 获取一次以后，再从debug的applicationContext获取一次

        Map<String,T> beansMap = applicationContext.getBeansOfType(clazz);

        Optional.ofNullable(debugSupport.getDebugApplicationContext())
                .ifPresent(applicationContext->{
                    beansMap.putAll(applicationContext.getBeansOfType(clazz));
                });
        return beansMap;
    }

    public List<NKCustomObjectDesc> getCustomObjectDescriptionList(Class<? extends NKCustomObject> clazz, boolean emptyValue, Predicate<Map.Entry<String,? extends NKCustomObject>> predicate){
        if(predicate==null){
            predicate = (e)-> true;
        }
        List<NKCustomObjectDesc> list = getCustomObjects(clazz)
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

    public <T extends NKCustomObject> T getCustomObject(String beanName, Class<T> clazz){
        Assert.isTrue(getApplicationContext().containsBean(beanName),String.format("自定义对象[%s]不存在或尚未激活",beanName));
        return getApplicationContext().getBean(beanName,clazz);
    }

    public <T extends NKCustomObject> T getCustomObjectIfExists(String beanName, Class<T> clazz){
        if(StringUtils.isNotBlank(beanName) && getApplicationContext().containsBean(beanName)){
            return getApplicationContext().getBean(beanName,clazz);
        }
        return null;
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
