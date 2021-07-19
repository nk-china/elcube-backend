package cn.nkpro.ts5.config.mvc;

import cn.nkpro.ts5.utils.TextUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * Created by bean on 2020/7/20.
 */
public class CompressReturnHandler implements HandlerMethodReturnValueHandler {

    private boolean responseCompress;
    public CompressReturnHandler(boolean responseCompress) {
        this.responseCompress = responseCompress;
    }

    @Override
    public boolean supportsReturnType(MethodParameter methodParameter) {
        return methodParameter.hasMethodAnnotation(JsonConfig.class)
                || methodParameter.hasMethodAnnotation(JsonConfigs.class)
                || methodParameter.hasMethodAnnotation(CompressResponse.class);
    }

    @Override
    public void handleReturnValue(@Nullable Object o,
                                  MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest) throws Exception {

        modelAndViewContainer.setRequestHandled(true);

        PropertyPreFilters filter = new PropertyPreFilters();

        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        Annotation[] annos = methodParameter.getMethodAnnotations();
        Arrays.asList(annos).forEach(anno -> {
            if (anno instanceof JsonConfig) {
                JsonConfig config = (JsonConfig) anno;
                filter.addFilter(config.type())
                        .addIncludes(config.includes())
                        .addExcludes(config.excludes());
            }
        });

        String output = o==null? StringUtils.EMPTY:JSONObject.toJSONString(o,filter.toFilters(), SerializerFeature.DisableCircularReferenceDetect);
        if(responseCompress && methodParameter.hasMethodAnnotation(CompressResponse.class)){
            output = TextUtils.compress(output);
        }

        assert response != null;
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(output);
    }

}
