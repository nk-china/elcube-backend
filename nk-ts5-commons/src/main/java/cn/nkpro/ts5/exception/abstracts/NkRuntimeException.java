package cn.nkpro.ts5.exception.abstracts;

/**
 * Created by bean on 2020/1/15.
 */
public abstract class NkRuntimeException extends RuntimeException {

    public NkRuntimeException(String message){
        super(message);
    }

    public NkRuntimeException(Throwable cause){
        super(cause);
    }

    public NkRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
