package cn.nkpro.ts5.engine.co;

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

    private ApplicationContext applicationContext;

    @Autowired@SuppressWarnings("all")
    private DebugContextManager applicationContextManager;

    public <T extends NkCustomObject> Map<String,T> getCustomObjects(Class<T> clazz){

        // 注意： 通过type从Spring上下文中获取bean时，只会从当前上下文中查找
        // 所以需要从 applicationContext 获取一次以后，再从debug的applicationContext获取一次

        Map<String,T> beansMap = applicationContext.getBeansOfType(clazz);

        if(applicationContextManager.getApplicationContext()!=applicationContext){
            beansMap.putAll(applicationContextManager.getApplicationContext().getBeansOfType(clazz));
        }
        return beansMap;
    }

    public List<NkCustomObjectDesc> getCustomObjectDescriptionList(Class<? extends NkCustomObject> clazz, boolean emptyValue, Predicate<Map.Entry<String,? extends NkCustomObject>> predicate){
        if(predicate==null){
            predicate = (e)-> true;
        }
        List<NkCustomObjectDesc> list = getCustomObjects(clazz)
                .entrySet()
                .stream()
                .filter(predicate)
                .map((e)->new NkCustomObjectDesc(e.getKey(),e.getValue().desc()))
                .sorted(Comparator.comparing(NkCustomObjectDesc::getName))
                .collect(Collectors.toList());
        if(emptyValue)
            list.add(0,new NkCustomObjectDesc(StringUtils.EMPTY,"空配置"));
        return list;
    }

    public <T extends NkCustomObject> T getCustomObject(String beanName, Class<T> clazz){
        Assert.isTrue(applicationContextManager.getApplicationContext().containsBean(beanName),String.format("自定义对象[%s]不存在或尚未激活",beanName));
        return applicationContextManager.getApplicationContext().getBean(beanName,clazz);
    }

    public <T extends NkCustomObject> T getCustomObjectIfExists(String beanName, Class<T> clazz){
        if(StringUtils.isNotBlank(beanName) && applicationContextManager.getApplicationContext().containsBean(beanName)){
            return applicationContextManager.getApplicationContext().getBean(beanName,clazz);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
