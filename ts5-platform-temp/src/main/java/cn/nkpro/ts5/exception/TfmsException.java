package cn.nkpro.ts5.exception;

/**
 * Created by bean on 2020/1/15.
 */
public class TfmsException extends RuntimeException {

    public TfmsException(String message){
        super(message);
    }

    public TfmsException(Throwable cause){
        super(cause);
    }

    public TfmsException(String message, Throwable cause) {
        super(message, cause);
    }
}
