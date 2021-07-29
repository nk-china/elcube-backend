package cn.nkpro.ts5.engine.script;

import cn.nkpro.ts5.basic.NKCustomObject;
import cn.nkpro.ts5.basic.NKCustomObjectManager;
import cn.nkpro.ts5.exception.TfmsException;
import cn.nkpro.ts5.utils.ClassUtils;
import groovy.lang.GroovyObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@Component
public class GroovyManager implements ApplicationContextAware {

    private ScriptEngineManager manager = new ScriptEngineManager();
    private ScriptEngine engine = manager.getEngineByName("groovy");

    private ApplicationContext applicationContext;
    private BeanDefinitionRegistry beanDefinitionRegistry;

    Class<?> compileGroovy(String groovyName, String groovyCode){

        Class<?> clazz;

        try {
            clazz = (Class<?>) engine.eval(groovyCode);

            if(!StringUtils.equals(clazz.getSimpleName(),groovyName)){
                throw new RuntimeException(
                        String.format("编译Groovy对象 [%s] 发生错误: 类名 %s 不一致",
                                groovyName,
                                clazz.getName()));
            }

        } catch (ScriptException e) {
            throw new RuntimeException(
                    String.format("编译Groovy对象 [%s] 发生错误: %s",
                            groovyName,
                            e.getMessage()),e);
        }

        return clazz;
    }

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
