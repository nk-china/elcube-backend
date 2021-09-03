package cn.nkpro.ts5.exception.abstracts;

/**
 * 警告
 */
public abstract class NkCaution extends RuntimeException {

    public NkCaution(String message) {
        super(message);
    }

    public NkCaution(Throwable cause) {
        super(cause);
    }

    public NkCaution(String message, Throwable cause) {
        super(message, cause);
    }
}
