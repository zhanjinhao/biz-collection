package cn.addenda.bc.bc;

import lombok.Getter;

/**
 * @author addenda
 * @since 2023/6/3 19:09
 */
public class ServiceException extends RuntimeException {

    @Getter
    private final int errorCode = 999_999;

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
