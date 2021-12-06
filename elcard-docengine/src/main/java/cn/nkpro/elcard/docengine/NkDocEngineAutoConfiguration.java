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
package cn.nkpro.elcard.docengine;

import cn.nkpro.elcard.docengine.service.NkDocEngineContext;
import cn.nkpro.elcard.docengine.service.SequenceSupport;
import cn.nkpro.elcard.docengine.service.sequences.DefaultSequenceSupportImpl;
import cn.nkpro.elcard.task.NkBpmTaskService;
import cn.nkpro.elcard.task.impl.DefaultBpmTaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@EnableConfigurationProperties(NkDocProperties.class)
@Configuration
public class NkDocEngineAutoConfiguration implements ApplicationRunner, WebMvcConfigurer {

    @Autowired
    private NkDocSearchService searchService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        searchService.init();
    }
    /**
     * 单据编号序列配置
     */
    @ConditionalOnMissingBean
    @Bean
    public SequenceSupport sequenceSupport(){
        return new DefaultSequenceSupportImpl();
    }


    @ConditionalOnMissingBean
    @Bean
    public NkBpmTaskService nkBpmTaskService(){
        return new DefaultBpmTaskServiceImpl();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ThreadLocalClearInterceptor());
    }

    public static class ThreadLocalClearInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            NkDocEngineContext.clear();
            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
            NkDocEngineContext.clear();
        }
    }
}
