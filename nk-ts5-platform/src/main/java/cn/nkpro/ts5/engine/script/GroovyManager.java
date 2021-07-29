package cn.nkpro.ts5.engine.script;

import cn.nkpro.ts5.engine.co.NKCustomObject;
import cn.nkpro.ts5.utils.ClassUtils;
import groovy.lang.GroovyObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class GroovyManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private BeanDefinitionRegistry beanDefinitionRegistry;

    void registerGroovyObject(Class<?> clazz){

        String beanName = ClassUtils.decapitateBeanName(clazz);

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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
    }
}
