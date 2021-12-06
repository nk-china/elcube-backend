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
package scriptTest;

import cn.nkpro.elcube.utils.JavaCompileUtils;
import org.junit.Test;

/**
 * Created by bean on 2021/11/21.
 */
public class JavaTest {

    @Test
    public void test() throws Exception {

        String str1 = "package scriptTest;\n" +
                "\n" +
                "public class UserGr implements User{\n" +
                "\n" +
                "    B b = new B();" +
                "    public Object run(){\n" +
                "        return b;\n" +
                "    }\n" +
                "\n" +
                "    private String test1(){\n" +
                "        String a = \"\";\n" +
                "        for(int i = 0; i<100; i++){\n" +
                "            a = String.valueOf(i);\n" +
                "        }\n" +
                "        return a;\n" +
                "    }\n" +
                "public static class B{public int a(){return 1;}}}\n";

        String str2 = "package scriptTest;\n" +
                "\n" +
                "public class UserGr implements User{\n" +
                "\n" +
                "    B b = new B();" +
                " \n" +
                "\n" +
                "    public Object run(){\n" +
                "        return new B();\n" +
                "    }\n" +
                "\n" +
                "    private String test1(){\n" +
                "        String a = \"\";\n" +
                "        for(int i = 0; i<100; i++){\n" +
                "            a += String.valueOf(i);\n" +
                "        }\n" +
                "        return a;\n" +
                "    }\n" +
                "public static class B{public int a(){return 123;}}}\n";

//        Class<?> clazzJava1 = GroovyUtils.compileGroovy("UserGr",str1);
//        Class<?> clazzJava2 = GroovyUtils.compileGroovy("UserGr",str2);


        Class<?> clazzJava1 = JavaCompileUtils.compile("scriptTest","UserGr",str1);
        Class<?> clazzJava2 = JavaCompileUtils.compile("scriptTest","UserGr",str2);

        System.out.println(clazzJava1);
        System.out.println(clazzJava2);
        System.out.println(clazzJava1==clazzJava2);
        System.out.println(((User)(clazzJava1.newInstance())).run());
        System.out.println(((User)(clazzJava2.newInstance())).run());
        System.out.println(((User)(clazzJava1.newInstance())).run().getClass());
        System.out.println(((User)(clazzJava2.newInstance())).run().getClass());
        System.out.println(((User)(clazzJava1.newInstance())).run().getClass()==((User)(clazzJava1.newInstance())).run().getClass());


        Object obj1 = ((User) (clazzJava1.newInstance())).run();
        Object obj2 = ((User) (clazzJava2.newInstance())).run();
        System.out.println(obj1.getClass().getDeclaredMethod("a").invoke(obj1));
        System.out.println(obj2.getClass().getDeclaredMethod("a").invoke(obj2));
    }
}
