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
package cn.nkpro.elcube.utils;

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
