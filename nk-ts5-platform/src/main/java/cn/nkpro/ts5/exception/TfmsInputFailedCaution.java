package cn.nkpro.ts5.exception;

import cn.nkpro.ts5.exception.abstracts.TfmsCaution;

/**
 * 输入内容校验失败的警告
 */
public class TfmsInputFailedCaution extends TfmsCaution {

    public TfmsInputFailedCaution(String message) {
        super(message);
    }
}