package cn.nkpro.easis.utils;

import org.apache.commons.lang3.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public final class GroovyUtils {

    private static ScriptEngineManager manager = new ScriptEngineManager();
    private static ScriptEngine engine = manager.getEngineByName("groovy");

    public static Class<?> compileGroovy(String groovyName, String groovyCode){

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
}
