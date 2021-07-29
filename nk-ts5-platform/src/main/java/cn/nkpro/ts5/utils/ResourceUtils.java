package cn.nkpro.ts5.utils;

import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by bean on 2021/7/15.
 */
public interface ResourceUtils {

    static String readText(Resource resource){

        StringBuilder groovyCode = new StringBuilder();

        InputStreamReader isr = null;
        BufferedReader br = null;

        try{
            isr = new InputStreamReader(resource.getInputStream());
            br = new BufferedReader(isr);
            String line;
            while((line=br.readLine())!=null){
                groovyCode.append(line);
                groovyCode.append('\n');
            }
        }catch (IOException e){
            throw new Error(e);
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
            if(isr!=null){
                try {
                    isr.close();
                } catch (IOException ignored) {
                }
            }
        }

        return groovyCode.toString();
    }
}
