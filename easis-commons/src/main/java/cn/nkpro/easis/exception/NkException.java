package cn.nkpro.easis.exception;

public class NkException extends Exception{

    public NkException(String message){
        super(message);
    }

    public NkException(Throwable cause){
        super(cause);
    }

    public NkException(String message, Throwable cause) {
        super(message, cause);
    }
}
