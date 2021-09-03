package cn.nkpro.ts5.docengine;

import cn.nkpro.ts5.exception.abstracts.NkRuntimeException;
import lombok.Getter;

/**
 *
 * 组件运行时异常：卡片，脚本等
 * Created by bean on 2020/1/15.
 */
public class TfmsComponentException extends NkRuntimeException {

    @Getter
    private NkCard card;

    public TfmsComponentException(NkCard card, Throwable cause) {
        super(String.format("组件[%s]发生错误：%s",card.getCardName(),cause.getMessage()), cause);
        this.card = card;
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
}
