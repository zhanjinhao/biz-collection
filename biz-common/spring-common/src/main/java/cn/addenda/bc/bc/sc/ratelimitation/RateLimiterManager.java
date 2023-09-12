package cn.addenda.bc.bc.sc.ratelimitation;

/**
 * @author addenda
 * @since 2023/9/12 18:58
 */
public interface RateLimiterManager {

    void create();

    void expire(String limiterName);

}
