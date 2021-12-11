/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.co;

import cn.nkpro.elcube.annotation.Keep;
import cn.nkpro.elcube.basic.Constants;
import cn.nkpro.elcube.data.redis.RedisSupport;
import cn.nkpro.elcube.exception.NkDebugContextNotFoundException;
import cn.nkpro.elcube.exception.NkSystemException;
import cn.nkpro.elcube.utils.GroovyUtils;
import cn.nkpro.elcube.utils.OsUtils;
import groovy.lang.GroovyObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *
 *
 * <i>此类的命名有些不合理，事实上它就是ApplicationContextManager
 *
 * <p>这个类管理着所有自定义对象的不同状态，激活与调试
 *
 * <p>甚至管理系统中运行中的调试Context
 *
 * <p>它根据上下文中的debug参数，来给运行时程序返回一个合理的Bean对象
 * 同时它接收运行时程序需要调试、或者取消调试的命令
 *
 * <p>根据 {@link #startThreadLocal(String)} 来启动当前线程为调试模式，从指定的调试上下文中获取运行实例Bean
 * <p>根据 {@link #addDebugResource(String, Object)} 向指定的调试上下文中覆盖一个需要调试的对象
 * <p>根据 {@link #addActiveResource(String, NkScriptV, boolean)} 向指定的调试上下文中覆盖一个需要激活的对象
 *
 *
 * <p>调试时，日志在前端的输出也是由它来处理，通过重写System.out，来截取线程中的log信息
 *
 */
@Component
public class DebugContextManager implements ApplicationContextAware {

    private final static Map<String, ApplicationContext> debugApplications = new ConcurrentHashMap<>();
    private final static ThreadLocal<String>                  localDebugId = new ThreadLocal<>();
    private final static ThreadLocal<ByteArrayOutputStream>    localOutput = new ThreadLocal<>();

    private ApplicationContext                  rootApplicationContext;

    @Autowired@SuppressWarnings("all")
    private RedisSupport<Object> redisForResoure;
    @Autowired@SuppressWarnings("all")
    private RedisSupport<ContextDescribe>       redisForContext;

    /**
     * 返回上下文列表
     */
    public Collection<ContextDescribe> getDebugContextList(){
        Map<String, ContextDescribe> absent = redisForContext.getHash(Constants.CACHE_DEBUG_CONTEXT);
        if(absent!=null){
            return absent.values().stream()
                    .peek(item-> item.setVisible(StringUtils.equals(item.getMac(),OsUtils.getMacAddress()))).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 创建上下文
     */
    public String createContext(String desc,String username){
        String debugId = UUID.randomUUID().toString();

        // 创建Spring上下文

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setParent(rootApplicationContext);
        context.refresh();
        debugApplications.put(debugId,context);

        // 创建调试上下文
        redisForContext.set(
                Constants.CACHE_DEBUG_CONTEXT,
                debugId,
                new ContextDescribe(debugId,OsUtils.getMacAddress(),username,desc,true)
        );

        return debugId;
    }

    /**
     * 停止一个上下文
     */
    public void removeContext(String debugId){
        ApplicationContext context = debugApplications.remove(debugId);
        if(context!=null){
            ((GenericApplicationContext)context).stop();
        }
        redisForResoure.delete(String.format("DEBUG:%s", debugId));
        redisForResoure.deleteHash(Constants.CACHE_DEBUG_CONTEXT,debugId);
    }

    /**
     * 请求开始时，初始化本地线程
     * 判断debugId是否存在，如果不存在则创建一个新的debug上下文，同时从redis中获取需要调试的资源，并初始化
     */
    void startThreadLocal(String debugId){

        // 从缓存中获取上下文描述
        ContextDescribe debugContext = redisForContext
                .getIfAbsent(Constants.CACHE_DEBUG_CONTEXT, debugId,() -> null);

        // 如果上下文不存在 抛出错误
        if(debugContext==null){
            throw new NkDebugContextNotFoundException("当前调试上下文不存在");
        }

        // 如果上下文的mac地址不一致 抛出错误
        if(!StringUtils.equals(OsUtils.getMacAddress(),debugContext.getMac())){
            throw new NkDebugContextNotFoundException("当前调试上下文与运行环境不一致");
        }

        // 检查Spring Application 是否已经启动，如果没有，则启动，并初始化debug对象
        if(!debugApplications.containsKey(debugId)){

            // 创建上下文
            AnnotationConfigApplicationContext debugApplicationContext = new AnnotationConfigApplicationContext();
            debugApplicationContext.setParent(rootApplicationContext);
            debugApplicationContext.refresh();

            debugApplications.put(debugId,debugApplicationContext);

            // 初始化debug对象
            Optional.ofNullable(redisForResoure.getHash(String.format("DEBUG:%s", debugId)))
                    .ifPresent((map)->
                        map.entrySet()
                                .stream()
                                .filter(e->e.getKey().startsWith("#"))
                                .map(Map.Entry::getValue)
                                .forEach(e->registerScriptObject((NkScriptV) e,debugApplicationContext, true))
                    );
        }

        // 设置本地线程
        localDebugId.set(debugId);
        localOutput.set(new ByteArrayOutputStream());
    }

    /**
     * 请求结束后，清理本地线程
     */
    String exitThreadLocal(){
        ByteArrayOutputStream output = null;
        try{
            output = localOutput.get();
            if(output!=null){
                return new String(output.toByteArray());
            }
            return null;
        }finally {
            localOutput.remove();
            localDebugId.remove();
            if(output!=null) {
                try {
                    output.flush();
                    output.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 向debug缓存中添加一个需要调试的资源，如：
     * 前缀 $：脚本Vue对象
     * 前缀 @：单据DocType配置
     */
    public void addDebugResource(String key, Object resource){

        ApplicationContext debugApplicationContext = Optional.ofNullable(localDebugId.get())
                .map(debugApplications::get)
                .orElseThrow(()->new NkSystemException("没有找到调试上下文"));

        if(resource instanceof NkScriptV){
            ((NkScriptV) resource).setDebug(true);
            registerScriptObject((NkScriptV) resource,debugApplicationContext,true);
        }else if(resource instanceof DebugAble){
            ((DebugAble) resource).setDebug(true);
        }else{
            throw new NkSystemException(resource.getClass().getName() + " 不支持调试");
        }

        redisForResoure.set(String.format("DEBUG:%s", localDebugId.get()), key, resource);
    }

    public void addActiveResource(String key, NkScriptV scriptDef, boolean rewrite){
        scriptDef.setDebug(false);
        registerScriptObject(scriptDef, rootApplicationContext,rewrite);
        removeDebugResource(key, scriptDef);
    }

    public void removeDebugResource(String key, Object resource){
        if(resource instanceof NkScriptV){
            ((NkScriptV) resource).setDebug(false);
            Optional.ofNullable(localDebugId.get())
                    .map(debugApplications::get)
                    .ifPresent(debugApplicationContext->{
                        String scriptName = ((NkScriptV) resource).getScriptName();
                        BeanDefinitionRegistry definitionRegistry = (BeanDefinitionRegistry)
                                debugApplicationContext.getAutowireCapableBeanFactory();
                        if(definitionRegistry.containsBeanDefinition(scriptName))
                            definitionRegistry.removeBeanDefinition(scriptName);
                    });
        }else if(resource instanceof DebugAble){
            ((DebugAble) resource).setDebug(false);
        }
        redisForResoure.deleteHash(String.format("DEBUG:%s", localDebugId.get()), key);
    }

    private void registerScriptObject(NkScriptV scriptDef, ApplicationContext context, boolean rewrite){

        Class<?> clazz = GroovyUtils.compileGroovy(scriptDef.getScriptName(), scriptDef.getGroovyMain());

        if(!ClassUtils.getAllInterfaces(clazz).contains(NkCustomScriptObject.class)){
            throw new RuntimeException("组件处理程序必须实现[ "+ NkCustomScriptObject.class.getSimpleName()+" ]");
        }

        String beanName = cn.nkpro.elcube.utils.ClassUtils.decapitateBeanName(clazz);

        Assert.isTrue(StringUtils.equals(beanName,scriptDef.getScriptName()),"对象名称与脚本名称必须保持一致");

        // 避免非法重写spring的类
        if(context.containsBean(beanName)){

            if(!rewrite){
                return;
            }

            Object exists = context.getBean(beanName);

            if(!(exists instanceof GroovyObject)){
                if(exists instanceof NkCustomObject){
                    if(((NkCustomObject) exists).isFinal()){
                        throw new RuntimeException(String.format("%s 不支持重写",exists.getClass().getName()));
                    }
                }else{
                    throw new RuntimeException(String.format("%s 不支持重写",exists.getClass().getName()));
                }
            }
        }

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        beanDefinitionBuilder.addPropertyValue("scriptDef", scriptDef);
        ((BeanDefinitionRegistry) context.getAutowireCapableBeanFactory())
                .registerBeanDefinition(beanName,beanDefinitionBuilder.getBeanDefinition());
    }

    public boolean isDebug(){
        return localDebugId.get()!=null;
    }

    /**
     * 获取当前debug的缓存中获取单个资源
     */
    @SuppressWarnings("all")
    public <T> T getDebugResource(String key){
        if(localDebugId.get()!=null){
            return (T) redisForResoure.getIfAbsent(String.format("DEBUG:%s", localDebugId.get()),key,()->null);
        }
        return null;
    }
    @SuppressWarnings("all")
    public <T> List<T> getDebugResources(String... keyPrefix){
        if(localDebugId.get()!=null){
            Map<String, Object> hashIfAbsent = redisForResoure.getHash(String.format("DEBUG:%s", localDebugId.get()));
            if(hashIfAbsent!=null)
                return hashIfAbsent
                    .entrySet()
                    .stream()
                    .filter(e ->
                         Arrays.stream(keyPrefix)
                                .filter(p->e.getKey().startsWith(p))
                                .findFirst()
                                .isPresent()
                    )
                    .map(e -> (T) e.getValue())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 获取当前debug的上下文对象
     */
    public ApplicationContext getApplicationContext() {
        return Optional.ofNullable(localDebugId.get())
                .map(debugApplications::get)
                .orElse(rootApplicationContext);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.rootApplicationContext = applicationContext;
        PrintStream systemOut = System.out;
        System.setOut(
                new PrintStream(
                        new OutputStream() {
                            @Override
                            public void write(int b) {
                                if (localOutput.get() != null)
                                    localOutput.get().write(b);
                                systemOut.write(b);
                            }
                        }
                )
        );
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Keep
    public static class ContextDescribe implements Serializable {
        private String id;
        private String mac;
        private String createUser;
        private String contextDesc;
        private boolean visible;
    }
}
