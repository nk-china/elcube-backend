package cn.nkpro.ts5.co;

import cn.nkpro.ts5.exception.abstracts.NkRuntimeException;
import lombok.Getter;

/**
 *
 * 组件运行时异常：卡片，脚本等
 * Created by bean on 2020/1/15.
 */
public class NkComponentException extends NkRuntimeException {

    @Getter
    private NkScriptComponent component;

    public NkComponentException(NkScriptComponent component, Throwable cause) {
        super(String.format("组件[%s]发生错误：%s",component.getName(),cause.getMessage()), cause);
        this.component = component;
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
}
