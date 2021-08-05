package cn.nkpro.ts5.exception;

public class TfmsException extends Exception{

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
