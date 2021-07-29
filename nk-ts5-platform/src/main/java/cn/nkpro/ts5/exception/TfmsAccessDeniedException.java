package cn.nkpro.ts5.exception;

/**
 * 用户没有访问权限
 * Created by bean on 2020/1/15.
 */
public class TfmsAccessDeniedException extends TfmsException {

    public TfmsAccessDeniedException(String msg){
        super(msg);
    }
}
