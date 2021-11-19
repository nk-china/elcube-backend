package cn.nkpro.ts5.docengine.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface ExceptionUtils {

    /**
     * 格式化异常信息
     * @param e Exception
     * @return 格式化的异常信息
     */
    default List<String> buildExceptionMessage(Throwable e){

        List<String> messages = new ArrayList<>();
        messages.add(String.format("<span class='t'>Caused by: %s: %s</span>",
                e.getClass().getName(),
                e.getLocalizedMessage()
        ));
        Arrays.stream(e.getStackTrace())
                .forEach(element-> messages.add(String.format("<span>at %s.%s(<span class='highlight'>%s:%d</span>)</span>",
                        element.getClassName(),
                        element.getMethodName(),
                        element.getFileName(),
                        element.getLineNumber()
                )));

        if(e.getCause()!=null){
            messages.addAll(buildExceptionMessage(e.getCause()));
        }

        return messages;
    }
}
