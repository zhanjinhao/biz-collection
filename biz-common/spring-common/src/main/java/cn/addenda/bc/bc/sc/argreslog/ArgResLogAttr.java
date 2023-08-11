package cn.addenda.bc.bc.sc.argreslog;

import lombok.*;

/**
 * @author addenda
 * @since 2023/3/9 10:17
 */
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArgResLogAttr {

    public static final String ERROR = "ERROR";
    public static final String INFO = "INFO";
    public static final String WARN = "WARN";

    public static final boolean DEFAULT_LOG_PARAM = true;
    public static final boolean DEFAULT_LOG_RESULT = true;
    public static final boolean DEFAULT_LOG_COST = true;
    public static final boolean DEFAULT_IGNORE_NULL_RESULT = false;
    public static final String DEFAULT_EXCEPTION_LEVEL = ERROR;

    @Builder.Default
    private boolean logParam = DEFAULT_LOG_PARAM;
    @Builder.Default
    private boolean logResult = DEFAULT_LOG_RESULT;
    @Builder.Default
    private boolean logCost = DEFAULT_LOG_COST;
    @Builder.Default
    private boolean ignoreNullResult = DEFAULT_IGNORE_NULL_RESULT;
    @Builder.Default
    private String exceptionLevel = DEFAULT_EXCEPTION_LEVEL;

}
