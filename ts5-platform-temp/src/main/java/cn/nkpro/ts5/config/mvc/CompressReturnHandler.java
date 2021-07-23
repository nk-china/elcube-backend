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
        Annotation[] annotations = methodParameter.getMethodAnnotations();
        Arrays.asList(annotations).forEach(annotation -> {
            if (annotation instanceof JsonConfig) {
                JsonConfig config = (JsonConfig) annotation;
                filter.addFilter(config.type())
                        .addIncludes(config.includes())
                        .addExcludes(config.excludes());
            }
        });

        String output;
        if(o==null){
            output = StringUtils.EMPTY;
        }else if(o instanceof CompressObject){
            output = o.toString();
        }else{
            output = JSONObject.toJSONString(o,filter.toFilters(), SerializerFeature.DisableCircularReferenceDetect);
        }

        if(responseCompress && methodParameter.hasMethodAnnotation(CompressResponse.class)){
            output = TextUtils.compress(output);
        }

        assert response != null;
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(output);
    }

}
