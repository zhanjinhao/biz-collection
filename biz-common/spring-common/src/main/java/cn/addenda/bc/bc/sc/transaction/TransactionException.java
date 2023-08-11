package cn.addenda.bc.bc.sc.transaction;

import cn.addenda.bc.bc.sc.SpringCommonException;

/**
 * @author addenda
 * @since 2022/3/2 23:04
 */
public class TransactionException extends SpringCommonException {

    public TransactionException() {
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }

    public TransactionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getComponentName() {
        return super.getComponentName() + "Transaction";
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode() + 4;
    }
}
