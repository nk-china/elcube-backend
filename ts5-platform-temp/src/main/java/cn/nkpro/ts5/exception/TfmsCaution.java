package cn.nkpro.ts5.exception;

/**
 * 警告
 */
public abstract class TfmsCaution extends TfmsException {

    public TfmsCaution(String message) {
        super(message);
    }

    public TfmsCaution(Throwable cause) {
        super(cause);
    }

    public TfmsCaution(String message, Throwable cause) {
        super(message, cause);
    }
}
