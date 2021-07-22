package cn.nkpro.ts5.controller.def;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.utils.ResourceUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/1/15.
 */
@WsDocNote("D1.卡片配置")
@Controller
@RequestMapping("/public/def/card")
public class SysCardResController {

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @PreAuthorize("permitAll")
    @WsDocNote("5、获取卡片信息")
    @ResponseBody
    @RequestMapping("/vueTemplates")
    public Map<String, String> vueTemplates() throws IOException {

        Resource[] resources = resourcePatternResolver.getResources("classpath*:/cn/nkpro/ts5/cards/**/*.vue");
        return Arrays.stream(resources)
                .collect(Collectors.toMap(
                        resource -> Objects.requireNonNull(resource.getFilename()).substring(0,resource.getFilename().length()-4),
                        ResourceUtils::readText
                ));
    }
}
