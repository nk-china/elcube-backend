package cn.nkpro.ts5.exception;

import cn.nkpro.ts5.exception.abstracts.NkRuntimeException;

/**
 *
 * 配置 或组件编码 不正确
 * Created by bean on 2020/1/15.
 */
public class NkDefineException extends NkRuntimeException {


    public NkDefineException(String message) {
        super(message);
    }

    public NkDefineException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
}
