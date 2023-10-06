package cn.addenda.bc.bc.sc.ratelimitation;

import cn.addenda.bc.bc.ServiceException;
import cn.addenda.bc.bc.jc.allocator.ExpiredAllocator;
import cn.addenda.bc.bc.jc.function.TSupplier;
import cn.addenda.bc.bc.jc.ratelimiter.RateLimiter;
import cn.addenda.bc.bc.jc.util.JacksonUtils;
import cn.addenda.bc.bc.jc.util.TimeUnitUtils;
import cn.addenda.bc.bc.rc.allocator.NamedExpiredAllocator;
import cn.addenda.bc.bc.sc.springcontext.ValueResolverHelper;
import cn.addenda.bc.bc.sc.util.SpELUtils;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2023/8/26 23:04
 */
public class RateLimitationSupport implements ApplicationContextAware, EnvironmentAware, InitializingBean {

    /**
     * {@link EnableRateLimitation#namespace()}
     */
    @Setter(value = AccessLevel.PROTECTED)
    private String namespace;

    @Setter
    protected String spELArgsName = "spELArgs";

    private Environment environment;

    private ApplicationContext applicationContext;

    private final Map<String, ExpiredAllocator<? extends RateLimiter>> map;

    public RateLimitationSupport(List<? extends NamedExpiredAllocator<? extends RateLimiter>> rateLimiterList) {
        map = rateLimiterList.stream().collect(Collectors.toMap(NamedExpiredAllocator::getName, a -> a));
    }

    protected Object invokeWithinRateLimitation(RateLimitationAttr attr, Object[] arguments, TSupplier<Object> supplier, Method method) throws Throwable {
        String spEL = attr.getSpEL();
        String key = SpELUtils.getKey(spEL, method, spELArgsName, arguments);

        if (key == null || key.length() == 0) {
            String msg = String.format("RateLimiterKey can not be null or \"\". arguments: [%s], spEL: [%s].",
                JacksonUtils.objectToString(arguments), spEL);
            throw new RateLimitationException(msg);
        }

        String rateLimiterAllocator = attr.getRateLimiterAllocator();
        ExpiredAllocator<? extends RateLimiter> expiredAllocator = map.get(rateLimiterAllocator);
        if (expiredAllocator == null) {
            throw new RateLimitationException("cannot get rateLimiterAllocator");
        }
        String rateLimiterKey = namespace + ":" + attr.getPrefix() + ":" + key;
        RateLimiter rateLimiter;
        long ttl = attr.getTtl();
        if (ttl == -1) {
            rateLimiter = expiredAllocator.allocate(rateLimiterKey);
        } else if (ttl == -2) {
            rateLimiter = expiredAllocator.allocateWithDefaultTtl(rateLimiterKey);
        } else {
            rateLimiter = expiredAllocator.allocate(rateLimiterKey, attr.getTimeUnit(), attr.getTtl());
        }

        if (rateLimiter.tryAcquire()) {
            return supplier.get();
        }

        Properties properties = new Properties();
        properties.put("prefix", attr.getPrefix());
        if (StringUtils.hasLength(spEL)) {
            properties.put("spEL", attr.getSpEL());
        }
        properties.put("timeUnit", attr.getTimeUnit());
        properties.put("ttl", attr.getTtl());
        properties.put("ttlStr", attr.getTtl() + " " + TimeUnitUtils.convertTimeUnit(attr.getTimeUnit()));
        properties.put("key", key);
        properties.put("simpleKey", attr.getPrefix() + ":" + key);
        properties.put("fullKey", rateLimiterKey);

        String rateLimitedMsg = attr.getRateLimitedMsg();
        String msg = ValueResolverHelper.resolveHashPlaceholder(rateLimitedMsg, properties);
        throw new ServiceException(msg);
    }

    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return this.environment.resolvePlaceholders(value);
        }
        return value;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.namespace = resolve(namespace);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
