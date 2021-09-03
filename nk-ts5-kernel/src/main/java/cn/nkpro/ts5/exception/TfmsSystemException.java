package cn.nkpro.ts5.exception;

import cn.nkpro.ts5.exception.abstracts.NkRuntimeException;

/**
 *
 * 系统异常
 * Created by bean on 2020/1/15.
 */
public class TfmsSystemException extends NkRuntimeException {


    public TfmsSystemException(String message) {
        super(message);
    }

    public TfmsSystemException(Throwable cause){
        super(cause);
    }

    public TfmsSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }

    public static TfmsSystemException of(String message){
        return new TfmsSystemException(message);
    }

    public static TfmsSystemException of(Throwable cause){
        return new TfmsSystemException(cause);
    }

    public static TfmsSystemException of(String message,Throwable cause){
        return new TfmsSystemException(message, cause);
    }
}
