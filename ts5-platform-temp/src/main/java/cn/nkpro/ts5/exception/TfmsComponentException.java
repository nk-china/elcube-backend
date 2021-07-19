package cn.nkpro.ts5.exception;

import lombok.Getter;

/**
 *
 * 组件异常
 * Created by bean on 2020/1/15.
 */
public class TfmsComponentException extends TfmsException {

    @Getter
    private String componentName;

    public TfmsComponentException(String componentName, Throwable cause) {
        super(String.format("组件[%s]发生错误：%s",componentName,cause.getMessage()), cause);
        this.componentName = componentName;
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
}
