package cn.nkpro.ts5.utils;

import cn.nkpro.ts5.utils.jc.CharSequenceJavaFileObject;
import cn.nkpro.ts5.utils.jc.JdkDynamicCompileClassLoader;
import cn.nkpro.ts5.utils.jc.JdkDynamicCompileJavaFileManager;

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
