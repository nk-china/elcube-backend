package cn.nkpro.ts5.exception;

import cn.nkpro.ts5.exception.abstracts.NkRuntimeException;

/**
 *
 * 系统异常
 * Created by bean on 2020/1/15.
 */
public class NkSystemException extends NkRuntimeException {


    public NkSystemException(String message) {
        super(message);
    }

    public NkSystemException(Throwable cause){
        super(cause);
    }

    public NkSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }

    public static NkSystemException of(String message){
        return new NkSystemException(message);
    }

    public static NkSystemException of(Throwable cause){
        return new NkSystemException(cause);
    }

    public static NkSystemException of(String message, Throwable cause){
        return new NkSystemException(message, cause);
    }
}
