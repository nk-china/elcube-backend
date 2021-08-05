package cn.nkpro.ts5.exception.abstracts;

/**
 * 警告
 */
public abstract class TfmsCaution extends RuntimeException {

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
