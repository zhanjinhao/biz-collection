package cn.addenda.bc.bc.sc.lock;

import cn.addenda.bc.bc.sc.SpringCommonException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author addenda
 * @since 2022/11/30 19:16
 */
public class LockException extends SpringCommonException {

    @Setter
    @Getter
    private String mode;
    public static final String BUSY = "BUSY";
    public static final String ERROR = "ERROR";

    public LockException() {
        this.mode = ERROR;
    }

    public LockException(String message) {
        super(message);
        this.mode = ERROR;
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
        this.mode = ERROR;
    }

    public LockException(Throwable cause) {
        super(cause);
        this.mode = ERROR;
    }

    public LockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.mode = ERROR;
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode() + 2;
    }

    @Override
    public String getComponentName() {
        return super.getComponentName() + "Lock";
    }

}
