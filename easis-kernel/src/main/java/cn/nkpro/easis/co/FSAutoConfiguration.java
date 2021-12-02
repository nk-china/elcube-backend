/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.co;

import cn.nkpro.easis.co.fs.FSSupport;
import cn.nkpro.easis.co.fs.defaults.DefaultFileSupportAliyunOSS;
import cn.nkpro.easis.co.fs.defaults.DefaultFileSupportHuaweiOBS;
import cn.nkpro.easis.co.fs.defaults.DefaultFileSupportImpl;
import cn.nkpro.easis.co.fs.properties.AliyunProperties;
import cn.nkpro.easis.co.fs.properties.HuaweiProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@EnableConfigurationProperties({
        AliyunProperties.class,
        HuaweiProperties.class,
})
public class FSAutoConfiguration {

    /**
     * 文件上传配置 华为OBS
     */
    @Order(0)
    @ConditionalOnMissingBean
    @ConditionalOnProperty("tfms.huawei.obs.bucket")
    @Bean
    public FSSupport defaultFileSupportHuaweiOBS(){
        return new DefaultFileSupportHuaweiOBS();
    }

    /**
     * 文件上传配置 阿里云OSS
     */
    @Order(1)
    @ConditionalOnMissingBean
    @ConditionalOnProperty("tfms.aliyun.oss.bucket")
    @Bean
    public FSSupport defaultAliyunOSSFileSupport(){
        return new DefaultFileSupportAliyunOSS();
    }

    /***
     * 文件上传配置 本地服务
     */
    @Order
    @ConditionalOnMissingBean
    @Bean
    public FSSupport defaultFileSupport(){
        return new DefaultFileSupportImpl();
    }
}
