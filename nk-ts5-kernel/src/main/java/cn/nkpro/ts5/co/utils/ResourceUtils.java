package cn.nkpro.ts5.co.utils;

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

        try (InputStreamReader isr = new InputStreamReader(resource.getInputStream()); BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                groovyCode.append(line);
                groovyCode.append('\n');
            }
        } catch (IOException e) {
            throw new Error(e);
        }

        return groovyCode.toString();
    }
}
