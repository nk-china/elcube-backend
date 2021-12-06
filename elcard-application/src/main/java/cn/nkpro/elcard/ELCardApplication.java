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
package cn.nkpro.elcard;

import cn.nkpro.elcard.basic.NkProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

/**
 * Created by bean on 2021/9/17.
 */
@SpringBootApplication
@EnableConfigurationProperties(NkProperties.class)
@ComponentScan(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
        basePackages = {"cn.nkpro.elcard"}
)
public class ELCardApplication {
    public static void main(String[] args) {
        SpringApplication.run(ELCardApplication.class, args);
    }
}
