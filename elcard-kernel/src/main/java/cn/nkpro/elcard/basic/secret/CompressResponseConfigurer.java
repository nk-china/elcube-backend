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
package cn.nkpro.elcard.basic.secret;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bean on 2019/12/30.
 */
@Configuration
@ConditionalOnProperty(prefix = "nk.compress", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(CompressProperties.class)
public class CompressResponseConfigurer implements InitializingBean {

    @Autowired@SuppressWarnings("all")
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Override
    public void afterPropertiesSet() {
        List<HandlerMethodReturnValueHandler> unmodifiableList = requestMappingHandlerAdapter.getReturnValueHandlers();
        assert unmodifiableList != null;
        List<HandlerMethodReturnValueHandler> list = new ArrayList<>(unmodifiableList.size());
        for (HandlerMethodReturnValueHandler returnValueHandler : unmodifiableList) {
            if (returnValueHandler instanceof RequestResponseBodyMethodProcessor) {
                list.add(new CompressReturnHandler(returnValueHandler));
            } else {
                list.add(returnValueHandler);
            }
        }
        requestMappingHandlerAdapter.setReturnValueHandlers(list);
    }
}
