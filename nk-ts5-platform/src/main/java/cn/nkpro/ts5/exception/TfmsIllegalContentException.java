package cn.nkpro.ts5.exception;

/**
 * Created by bean on 2020/1/15.
 */
public class TfmsIllegalContentException extends TfmsException {

    public TfmsIllegalContentException(String msg){
        super(msg);
    }

    public TfmsIllegalContentException(String msg,Exception e){
        super(msg,e);
    }
}
