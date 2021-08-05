package cn.nkpro.ts5.exception;

import cn.nkpro.ts5.exception.abstracts.TfmsException;

/**
 *
 * 配置 或组件编码 不正确
 * Created by bean on 2020/1/15.
 */
public class TfmsDefineException extends TfmsException {


    public TfmsDefineException(String message) {
        super(message);
    }

    public TfmsDefineException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
}
