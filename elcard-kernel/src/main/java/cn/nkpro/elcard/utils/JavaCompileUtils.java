/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.utils;

import cn.nkpro.elcard.utils.jc.CharSequenceJavaFileObject;
import cn.nkpro.elcard.utils.jc.JdkDynamicCompileClassLoader;
import cn.nkpro.elcard.utils.jc.JdkDynamicCompileJavaFileManager;

import javax.tools.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bean on 2021/11/21.
 */
public class JavaCompileUtils {

    private static DiagnosticCollector<JavaFileObject> DIAGNOSTIC_COLLECTOR = new DiagnosticCollector<>();
    private static JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    private static StandardJavaFileManager javaFileManager = compiler.getStandardFileManager(DIAGNOSTIC_COLLECTOR, null, null);


    public static Class<?> compile(String packageName, String name,String source) throws ClassNotFoundException {


        JdkDynamicCompileClassLoader classLoader = new JdkDynamicCompileClassLoader(Thread.currentThread().getContextClassLoader());
        JdkDynamicCompileJavaFileManager fileManager = new JdkDynamicCompileJavaFileManager(javaFileManager, classLoader);

        List<String> options = new ArrayList<>();
        options.add("-source");
        options.add(System.getProperty("java.specification.version"));
        options.add("-target");
        options.add(System.getProperty("java.specification.version"));

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, Arrays.asList(
                new CharSequenceJavaFileObject(name,source)
        ));

        if(task.call()){
            return classLoader.findClass(packageName+'.'+name);
        }
        return null;
    }
}
