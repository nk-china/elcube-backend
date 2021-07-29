package cn.nkpro.ts5.config.global.pagable;

import com.alibaba.fastjson.JSONArray;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class PagedListReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getParameterType()==PagedList.class;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

        mavContainer.setRequestHandled(true);

        assert response != null;
        try (PrintWriter writer = response.getWriter()) {

            if (returnValue != null) {

                PagedList list = (PagedList) returnValue;

                response.setHeader("NK-Paged-Start", String.valueOf(list.getStart()));
                response.setHeader("NK-Paged-Total", String.valueOf(list.getTotal()));
                response.setHeader("NK-Paged-Rows", String.valueOf(list.getRows()));
                writer.write(JSONArray.toJSONString(returnValue));

            } else {

                response.setHeader("NK-Paged-Start", String.valueOf(0));
                response.setHeader("NK-Paged-Total", String.valueOf(0));
                response.setHeader("NK-Paged-Rows", String.valueOf(0));
                writer.write(new JSONArray().toJSONString());
            }

            writer.flush();
        }
    }
}