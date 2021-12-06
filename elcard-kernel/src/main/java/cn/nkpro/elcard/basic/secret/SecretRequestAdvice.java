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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

@Slf4j
@ControllerAdvice
@ConditionalOnProperty(prefix = "faster.secret", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({SecretProperties.class})
@Order(1)
public class SecretRequestAdvice extends RequestBodyAdviceAdapter {

    @Autowired
    private SecretProperties secretProperties;


    /**
     * 是否支持加密消息体
     *
     * @param methodParameter methodParameter
     * @return true/false
     */
    private boolean supportSecretRequest(MethodParameter methodParameter) {
        if (!secretProperties.isScanAnnotation()) {
            return true;
        }
        //判断class是否存在注解
        if (methodParameter.getContainingClass().getAnnotation(secretProperties.getAnnotationClass()) != null) {
            return true;
        }
        //判断方法是否存在注解
        return methodParameter.getMethodAnnotation(secretProperties.getAnnotationClass()) != null;
    }


    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        //如果支持加密消息，进行消息解密。
        boolean supportSafeMessage = supportSecretRequest(parameter);
        String httpBody;
        if (supportSafeMessage) {
            httpBody = decryptBody(inputMessage);
            if (httpBody == null) {
                throw new HttpMessageNotReadableException("request body decrypt error",inputMessage);
            }
        } else {
            httpBody = StreamUtils.copyToString(inputMessage.getBody(), Charset.defaultCharset());
        }
        //返回处理后的消息体给messageConvert
        return new SecretHttpMessage(new ByteArrayInputStream(httpBody.getBytes()), inputMessage.getHeaders());
    }

    /**
     * 解密消息体,3des解析（cbc模式）
     *
     * @param inputMessage 消息体
     * @return 明文
     */
    private String decryptBody(HttpInputMessage inputMessage) throws IOException {
        InputStream encryptStream = inputMessage.getBody();
        String encryptBody = StreamUtils.copyToString(encryptStream, Charset.defaultCharset());
        return DesCbcUtil.decode(encryptBody, secretProperties.getDesSecretKey(), secretProperties.getDesIv());
    }
}