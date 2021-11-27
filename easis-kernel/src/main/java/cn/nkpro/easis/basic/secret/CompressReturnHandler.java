package cn.nkpro.easis.basic.secret;

import cn.nkpro.easis.utils.TextUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Created by bean on 2020/7/20.
 */
public class CompressReturnHandler implements HandlerMethodReturnValueHandler {

    private final HandlerMethodReturnValueHandler delegate;

    CompressReturnHandler(HandlerMethodReturnValueHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return delegate.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(@Nullable Object returnValue,
                                  MethodParameter returnType,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {

        returnValue = returnValue==null ? StringUtils.EMPTY :
                TextUtils.compress(JSONObject.toJSONString(returnValue));

        delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

}
