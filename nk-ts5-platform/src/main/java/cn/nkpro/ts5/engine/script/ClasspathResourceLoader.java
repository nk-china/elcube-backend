package cn.nkpro.ts5.engine.script;

import cn.nkpro.ts5.config.global.NKProperties;
import cn.nkpro.ts5.utils.ResourceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ClasspathResourceLoader {

    @Autowired
    @SuppressWarnings("all")
    private NKProperties properties;

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    Map<String, String> getVueMapFromClasspath() {

        List<Resource> resources = new ArrayList<>();

        Arrays.stream(properties.getVueBasePackages())
                .forEach(path->{
                    try {
                        path = path.replaceAll("[.]","/");
                        resources.addAll(Arrays.asList(resourcePatternResolver.getResources(String.format("classpath*:/%s/**/**.vue",packageToPath(path)))));
                    } catch (IOException ignored) {
                    }
                });

        return resources.stream()
                .collect(Collectors.toMap(
                        resource -> Objects.requireNonNull(resource.getFilename()).substring(0,resource.getFilename().length()-4),
                        ResourceUtils::readText
                ));
    }

    List<String> findResource(String resourceName){
        try {
            for (String path : properties.getVueBasePackages()) {
                Resource[] resources = resourcePatternResolver.getResources(String.format("classpath*:/%s/**/%s", packageToPath(path), resourceName));

                return Arrays.stream(resources)
                        .map(ResourceUtils::readText)
                        .collect(Collectors.toList());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private String packageToPath(String packageName){
        packageName = packageName.replaceAll("[.]","/");
        if(packageName.startsWith("/")){
            packageName = packageName.substring(1);
        }
        if(packageName.endsWith("/")){
            packageName = packageName.substring(0,packageName.length()-1);
        }
        return packageName;
    }
}
