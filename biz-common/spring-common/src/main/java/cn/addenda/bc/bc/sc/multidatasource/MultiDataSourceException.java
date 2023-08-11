package cn.addenda.bc.bc.sc.multidatasource;

import cn.addenda.bc.bc.sc.SpringCommonException;

/**
 * @author addenda
 * @since 2022/3/3 17:26
 */
public class MultiDataSourceException extends SpringCommonException {

    public MultiDataSourceException() {
    }

    public MultiDataSourceException(String message) {
        super(message);
    }

    public MultiDataSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultiDataSourceException(Throwable cause) {
        super(cause);
    }

    public MultiDataSourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode() + 1;
    }

    @Override
    public String getComponentName() {
        return super.getComponentName() + "MultiDataSource";
    }
}
