package cn.addenda.bc.bc.sc.result;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 请求异常时：
 * <p>
 * 业务异常时：
 *
 * @author addenda
 * @since 2022/2/7 16:49
 */
@Setter
@Getter
@ToString
public class ControllerResult<T> implements Serializable {

    private String reqId;
    private String version;
    private long ts = System.currentTimeMillis();
    private transient T result;

    /**
     * 请求状态
     */
    private Status reqStatus = Status.SYSTEM_EXCEPTION;
    /**
     * 请求失败代码
     */
    private Integer reqFailedCode;
    /**
     * 请求错误原因
     */
    private String reqAttachment;

    public ControllerResult() {
    }

    /**
     * 用于简便构建 请求成功&业务成功 时的结果对象
     */
    public ControllerResult(T result) {
        this.reqStatus = Status.SUCCESS;
        this.result = result;
    }

    public static <T> ControllerResult<T> success(T result) {
        return new ControllerResult<>(result);
    }

    public static <R, T> ControllerResult<R> success(T result, Function<T, R> function) {
        return new ControllerResult<>(function.apply(result));
    }

}
