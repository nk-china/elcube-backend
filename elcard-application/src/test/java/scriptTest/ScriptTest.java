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
package scriptTest;

import cn.nkpro.elcard.utils.JavaCompileUtils;
import org.junit.Test;

import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Created by bean on 2021/11/21.
 */
public class ScriptTest  {


    private static ScriptEngineManager manager = new ScriptEngineManager();
    private static ScriptEngine engineGr = manager.getEngineByName("groovy");


    Class<?> clazzJava;
    Class<?> clazzGroovy1;
    Class<?> clazzGroovy2;
    @Test
    public void test() throws Exception {


        String str = "package scriptTest;\n" +
                "\n" +
                "public class UserGr implements User{\n" +
                "\n" +
                "    int num = 0;\n" +
                "\n" +
                "    public int run(){\n" +
                "        for(int i = 0; i<10000000; i++){\n" +
                "            num += i;\n" +
                "        }\n" +
                "        test1().length();\n" +
                "        return num;\n" +
                "    }\n" +
                "\n" +
                "    private String test1(){\n" +
                "        String a = \"\";\n" +
                "        for(int i = 0; i<100; i++){\n" +
                "            a += String.valueOf(i);\n" +
                "        }\n" +
                "        return a;\n" +
                "    }\n" +
                "}";


        run(()->{
            clazzJava = JavaCompileUtils.compile("scriptTest","UserGr",str);
        });

        run(()->{
            clazzGroovy1 = (Class<?>) ((Compilable) engineGr).compile(str).eval();
        });

        run(()->{
            clazzGroovy2 = (Class<?>) engineGr.eval(str);
        });

        run(()->{
            User user = (User) clazzJava.newInstance();
            for(int i=0;i<10000;i++){
                user.run();
            }
        });
        run(()->{
            User user = (User) clazzGroovy1.newInstance();
            for(int i=0;i<10000;i++) {
                user.run();
            }
        });
        run(()->{
            User user = (User) clazzGroovy2.newInstance();
            for(int i=0;i<10000;i++) {
                user.run();
            }
        });



    }

    private void run(Function function) throws Exception {
        long start = System.currentTimeMillis();
        function.apply();
        long end = System.currentTimeMillis();
        System.out.println((end-start));
    }

    @FunctionalInterface
    interface Function{
        void apply() throws Exception;
    }


}
