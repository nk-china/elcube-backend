package cn.nkpro.ts5.exception;

import cn.nkpro.ts5.exception.abstracts.TfmsException;

/**
 *
 * 系统异常
 * Created by bean on 2020/1/15.
 */
public class TfmsSystemException extends TfmsException {


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
}
