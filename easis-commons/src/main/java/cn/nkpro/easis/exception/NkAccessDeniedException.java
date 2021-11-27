package cn.nkpro.easis.exception;

import cn.nkpro.easis.exception.abstracts.NkRuntimeException;

/**
 * 用户没有访问权限
 * Created by bean on 2020/1/15.
 */
public class NkAccessDeniedException extends NkRuntimeException {

    public NkAccessDeniedException(String msg){
        super(msg);
    }
}
