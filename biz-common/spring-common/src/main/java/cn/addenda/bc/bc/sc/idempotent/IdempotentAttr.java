package cn.addenda.bc.bc.sc.idempotent;

import lombok.*;

import java.util.concurrent.TimeUnit;

/**
 * @author addenda
 * @since 2023/7/29 14:03
 */
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdempotentAttr {

    public static final String DEFAULT_PREFIX = "prefix";

    public static final String DEFAULT_REPEAT_CONSUMPTION_MSG = "数据 [${key}] 已处理过！";

    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    public static final String DEFAULT_STORAGE_CENTER = "redisStorageCenter";

    public static final int DEFAULT_TIME_OUT = 60 * 60 * 24;
    public static final int DEFAULT_EXPECTED_COST = 1;

    @Builder.Default
    private String prefix = DEFAULT_PREFIX;

    private String spEL;

    @Builder.Default
    private String repeatConsumptionMsg = DEFAULT_REPEAT_CONSUMPTION_MSG;

    /**
     * 对于REST场景来说，如果数据已经消费过，对外抛出BusinessException。<br/>
     * 对于MQ场景来说，如果数据已经消费过，打印ERROR日志。
     */
    private IdempotentScenario scenario;

    @Builder.Default
    private String storageCenter = DEFAULT_STORAGE_CENTER;

    @Builder.Default
    private ConsumeMode consumeMode = ConsumeMode.SUCCESS;

    @Builder.Default
    private TimeUnit timeUnit = DEFAULT_TIME_UNIT;

    @Builder.Default
    private int expectedCost = DEFAULT_EXPECTED_COST;

    @Builder.Default
    private int timeout = DEFAULT_TIME_OUT;

}
