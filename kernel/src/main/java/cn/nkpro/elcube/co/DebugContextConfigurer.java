/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.co;

import cn.nkpro.elcube.data.redis.RedisSupport;
import cn.nkpro.elcube.utils.UUIDHexGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Created by bean on 2019/12/30.
 */
@Configuration
public class DebugContextConfigurer implements WebMvcConfigurer {

    @Autowired@SuppressWarnings("all")
    private DebugContextManager applicationContextManager;

    @Autowired
    private RedisSupport<String> redis;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DebugHandlerInterceptor());
    }

    class DebugHandlerInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            String debugId = request.getHeader("NK-Debug");
            if(StringUtils.isNotBlank(debugId)){
                response.setHeader("NK-Debug-Log", UUIDHexGenerator.generate());
                applicationContextManager.startThreadLocal(debugId);
            }
            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
            String output = applicationContextManager.exitThreadLocal();
            String logId = response.getHeader("NK-Debug-Log");
            if(output!=null&&logId!=null){
                redis.set(logId,output);
                redis.expire(logId,30);
            }
        }
    }
}
