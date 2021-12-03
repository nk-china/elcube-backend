/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.co;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * 自定义对象管理类
 *
 * 它是Spring上下文工厂的代理，由于系统支持多上下文的原因，所有运行时对象从Spring Context中获取时，不能直接调用 {@link BeanFactory#getBean(String)}
 * 等一系列方法。
 *
 * 多上下文的目的是为了不同的用户可以调试不同的配置及脚本，参考{@link DebugContextManager}
 *
 * 当然了，这个类也做了一些Bean是否存在的基础校验，省去了很多null指针判断的麻烦
 *
 *
 * Created by bean on 2020/7/24.
 */
@Component
public class NkCustomObjectManager implements ApplicationContextAware {

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
                .map((e)->new NkCustomObjectDesc(e.getKey(),e.getValue().desc(),e.getValue().order()))
                .sorted((o1,o2)->{
                    if(o1.getOrder()==o2.getOrder()){
                        return o1.getName().compareTo(o2.getName());
                    }
                    return o1.getOrder()-o2.getOrder();
                })
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

    public void assertExists(String beanName){
        Assert.isTrue(applicationContextManager.getApplicationContext().containsBean(beanName),String.format("自定义对象[%s]不存在或尚未激活",beanName));
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
