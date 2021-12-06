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
package cn.nkpro.elcard.co.fs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "nk.aliyun")
public class AliyunProperties {

    private String accessKeyId;
    private String accessKeySecret;

    @NestedConfigurationProperty
    private OSS oss;

    @Data
    public static class OSS{
        private String bucket = "tfms";
        private String endpoint = "oss-cn-beijing.aliyuncs.com";
        private String host = "https://tfms.oss-cn-beijing.aliyuncs.com";
        /**
         * 上传到OSS的根目录路径，不能以/开头，必须以/结尾
         */
        private String path = "";
    }
}
