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
package cn.nkpro.elcard.utils.jc;

import javax.tools.JavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JdkDynamicCompileClassLoader extends ClassLoader {

    private static final String CLASS_EXTENSION = ".class";

    private final Map<String, JavaFileObject> javaFileObjectMap = new HashMap<>();

    public JdkDynamicCompileClassLoader(ClassLoader parentClassLoader) {
        super(parentClassLoader);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        JavaFileObject javaFileObject = javaFileObjectMap.get(name);
        if (null != javaFileObject) {
            CharSequenceJavaFileObject charSequenceJavaFileObject = (CharSequenceJavaFileObject) javaFileObject;
            byte[] byteCode = charSequenceJavaFileObject.getByteCode();
            return defineClass(name, byteCode, 0, byteCode.length);
        }
        return super.findClass(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        if (name.endsWith(CLASS_EXTENSION)) {
            String qualifiedClassName = name.substring(0, name.length() - CLASS_EXTENSION.length()).replace('/', '.');
            CharSequenceJavaFileObject javaFileObject = (CharSequenceJavaFileObject) javaFileObjectMap.get(qualifiedClassName);
            if (null != javaFileObject && null != javaFileObject.getByteCode()) {
                return new ByteArrayInputStream(javaFileObject.getByteCode());
            }
        }
        return super.getResourceAsStream(name);
    }

    /**
     * 暂时存放编译的源文件对象,key为全类名的别名（非URI模式）,如club.throwable.compile.HelloService
     */
    void addJavaFileObject(String qualifiedClassName, JavaFileObject javaFileObject) {
        javaFileObjectMap.put(qualifiedClassName, javaFileObject);
    }

    Collection<JavaFileObject> listJavaFileObject() {
        return Collections.unmodifiableCollection(javaFileObjectMap.values());
    }
}