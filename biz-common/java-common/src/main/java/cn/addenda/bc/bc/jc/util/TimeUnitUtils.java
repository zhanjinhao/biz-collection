package cn.addenda.bc.bc.jc.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author addenda
 * @since 2023/9/2 15:35
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeUnitUtils {

    public static final Map<TimeUnit, String> map = new EnumMap<>(TimeUnit.class);

    static {
        map.put(TimeUnit.NANOSECONDS, "ns");
        map.put(TimeUnit.MICROSECONDS, "μs");
        map.put(TimeUnit.MILLISECONDS, "ms");
        map.put(TimeUnit.SECONDS, "s");
        map.put(TimeUnit.MINUTES, "min(s)");
        map.put(TimeUnit.HOURS, "hour(s)");
        map.put(TimeUnit.DAYS, "day(s)");
    }

    public static String convertTimeUnit(TimeUnit timeUnit) {
        return map.get(timeUnit);
    }

}
