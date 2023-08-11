package cn.addenda.bc.bc;

/**
 * @author addenda
 * @since 2023/5/29 22:13
 */
public class SystemException extends RuntimeException {

    private final int errorCode;

    private final String componentName;

    protected SystemException() {
        super();
        this.errorCode = getErrorCode();
        this.componentName = getComponentName();
    }

    protected SystemException(String message) {
        super(message);
        this.errorCode = getErrorCode();
        this.componentName = getComponentName();
    }

    protected SystemException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = getErrorCode();
        this.componentName = getComponentName();
    }

    protected SystemException(Throwable cause) {
        super(cause);
        this.errorCode = getErrorCode();
        this.componentName = getComponentName();
    }

    protected SystemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = getErrorCode();
        this.componentName = getComponentName();
    }

    /**
     * 6位数字：前三位是包，后三位是包内的异常
     */
    public int getErrorCode() {
        return -1;
    }

    /**
     * 格式：模块名: 功能名。例如：java-common: Cache
     */
    public String getComponentName() {
        return "system";
    }

    @Override
    public String toString() {
        return super.toString() + " {" +
                "errorCode=" + errorCode +
                ", componentName='" + componentName + '\'' +
                "} ";
    }

    public static SystemException unExpectedException() {
        return new SystemException("unExpected exception!");
    }

}
