package cn.nkpro.ts5.exception.abstracts;

/**
 * Created by bean on 2020/1/15.
 */
public abstract class TfmsRuntimeException extends RuntimeException {

    public TfmsRuntimeException(String message){
        super(message);
    }

    public TfmsRuntimeException(Throwable cause){
        super(cause);
    }

    public TfmsRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
