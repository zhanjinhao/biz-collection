package cn.addenda.bc.bc.sc.lock;

import lombok.*;

import java.util.concurrent.TimeUnit;

/**
 * @author addenda
 * @since 2022/12/1 19:05
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LockedAttr {

    public static final String DEFAULT_PREFIX = "prefix";
    public static final String DEFAULT_LOCK_FAILED_MSG = "数据 [{}] 繁忙，请重试！";

    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;

    public static final long DEFAULT_WAIT_TIME = 500L;

    @Builder.Default
    private String prefix = DEFAULT_PREFIX;

    private String spEL;

    @Builder.Default
    private String lockFailedMsg = DEFAULT_LOCK_FAILED_MSG;

    @Builder.Default
    private TimeUnit timeUnit = DEFAULT_TIME_UNIT;

    /**
     * 锁冲突时等待多久。
     */
    @Builder.Default
    private long waitTime = DEFAULT_WAIT_TIME;

}
