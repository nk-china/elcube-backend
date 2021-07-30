package cn.nkpro.ts5.config.global;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(NkProperties.class)
public class NkPropertiesConfigurer implements WebMvcConfigurer {
}
