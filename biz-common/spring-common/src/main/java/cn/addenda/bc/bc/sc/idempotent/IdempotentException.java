package cn.addenda.bc.bc.sc.idempotent;

/**
 * @author addenda
 * @since 2023/7/29 19:07
 */
public class IdempotentException extends RuntimeException {

    public IdempotentException() {
    }

    public IdempotentException(String message) {
        super(message);
    }

    public IdempotentException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdempotentException(Throwable cause) {
        super(cause);
    }

    public IdempotentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
