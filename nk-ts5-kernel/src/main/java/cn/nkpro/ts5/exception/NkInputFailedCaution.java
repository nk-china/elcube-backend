package cn.nkpro.ts5.exception;

import cn.nkpro.ts5.exception.abstracts.NkCaution;

/**
 * 输入内容校验失败的警告
 */
public class NkInputFailedCaution extends NkCaution {

    public NkInputFailedCaution(String message) {
        super(message);
    }
}