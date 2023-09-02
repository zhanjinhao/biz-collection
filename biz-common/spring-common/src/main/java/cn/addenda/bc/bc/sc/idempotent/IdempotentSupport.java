package cn.addenda.bc.bc.sc.idempotent;

import cn.addenda.bc.bc.ServiceException;
import cn.addenda.bc.bc.jc.function.TSupplier;
import cn.addenda.bc.bc.jc.util.JacksonUtils;
import cn.addenda.bc.bc.jc.util.SleepUtils;
import cn.addenda.bc.bc.jc.util.TimeUnitUtils;
import cn.addenda.bc.bc.sc.idempotent.storagecenter.IdempotentParamWrapper;
import cn.addenda.bc.bc.sc.idempotent.storagecenter.StorageCenter;
import cn.addenda.bc.bc.sc.springcontext.ValueResolverHelper;
import cn.addenda.bc.bc.sc.util.SpELUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author addenda
 * @since 2023/7/29 14:11
 */
@Slf4j
public class IdempotentSupport implements EnvironmentAware, InitializingBean, ApplicationContextAware {

    /**
     * {@link EnableIdempotent#namespace()}
     */
    @Setter
    private String namespace;

    private Environment environment;

    private ApplicationContext applicationContext;

    @Setter
    protected String spELArgsName = "spELArgs";

    private final Map<String, StorageCenter> storageCenterMap = new ConcurrentHashMap<>();

    public Object invokeWithIdempotent(IdempotentAttr attr, Object[] arguments, TSupplier<Object> supplier, Method method) throws Throwable {

        String spEL = attr.getSpEL();
        String key = SpELUtils.getKey(spEL, method, spELArgsName, arguments);

        if (key == null || key.length() == 0) {
            String msg = String.format("Data Id of idempotent operation can not be null or \"\". arguments: [%s], spEL: [%s].",
                JacksonUtils.objectToString(arguments), spEL);
            throw new IdempotentException(msg);
        }

        ConsumeMode consumeMode = attr.getConsumeMode();
        IdempotentParamWrapper param = IdempotentParamWrapper.builder()
            .namespace(namespace)
            .prefix(attr.getPrefix())
            .key(key)
            .consumeMode(consumeMode)
            .timeoutSecs((int) attr.getTimeUnit().toSeconds(attr.getTimeout()))
            .build();

        switch (consumeMode) {
            case SUCCESS:
                return handleSuccessMode(param, attr, supplier, true, arguments);
            case COMPLETE:
                return handleCompleteMode(param, attr, supplier, arguments);
            default: // unreachable
                return null;
        }
    }

    /**
     * 设置{@link ConsumeStatus#CONSUMING}状态失败时：
     * <ul><li>
     * 如果是REST模式，需要将异常扔给用户；
     * </li>
     * <li>
     * 如果是MQ模式，需要记录消息
     * </li></ul>
     * 具体实现放在{@link StorageCenter#exceptionCallback(IdempotentParamWrapper, IdempotentScenario, Object[], Throwable)}里。
     */
    private boolean setConsumingStatusIfAbsent(IdempotentParamWrapper param, IdempotentAttr attr, StorageCenter storageCenter, Object[] arguments) throws Throwable {
        try {
            return storageCenter.saveIfAbsent(param, ConsumeStatus.CONSUMING);
        } catch (Throwable t) {
            storageCenter.exceptionCallback(param, attr.getScenario(), arguments, t);
            throw t;
        }
    }

    /**
     * 数据当前状态无论是 {@link ConsumeStatus#CONSUMING}/{@link ConsumeStatus#SUCCESS}/{@link ConsumeStatus#EXCEPTION}，都认为消费过。
     */
    private Object handleCompleteMode(IdempotentParamWrapper param, IdempotentAttr attr,
                                      TSupplier<Object> supplier, Object[] arguments) throws Throwable {
        String storageCenterName = attr.getStorageCenter();
        StorageCenter storageCenter = storageCenterMap.computeIfAbsent(storageCenterName, s -> (StorageCenter) applicationContext.getBean(storageCenterName));
        boolean b = setConsumingStatusIfAbsent(param, attr, storageCenter, arguments);

        if (b) {
            return consume(storageCenter, supplier, param, attr, arguments);
        } else {
            return repeatConsume(param, attr, arguments);
        }
    }

    /**
     * 对于新数据：<br/>
     * 开始消费之前，将数据置为 {@link ConsumeStatus#CONSUMING}。 <br/>
     * 消费正常完成，将数据置为 {@link ConsumeStatus#SUCCESS}。 <br/>
     * 消费异常完成，将数据置为 {@link ConsumeStatus#EXCEPTION}。
     * <p>
     * <p/>
     * 对于重复消费数据：<br/>
     * 如果上一条数据是正在消费中，等待后重试。 <br/>
     * 如果上一条数据是消费异常完成，本次按新数据消费。 <br/>
     * 如果上一条数据是消费正常完成，本次消费进入重复消费逻辑。
     */
    private Object handleSuccessMode(IdempotentParamWrapper param, IdempotentAttr attr,
                                     TSupplier<Object> supplier, boolean retry, Object[] arguments) throws Throwable {
        String storageCenterName = attr.getStorageCenter();
        StorageCenter storageCenter = storageCenterMap.computeIfAbsent(storageCenterName, s -> (StorageCenter) applicationContext.getBean(storageCenterName));
        boolean b = setConsumingStatusIfAbsent(param, attr, storageCenter, arguments);

        if (b) {
            return consume(storageCenter, supplier, param, attr, arguments);
        } else {
            ConsumeStatus consumeStatus = storageCenter.get(param);
            switch (consumeStatus) {
                case EXCEPTION:
                    storageCenter.modifyStatus(param, ConsumeStatus.CONSUMING);
                    log.info("[{}] has consumed exceptionally. re-consume it. Mode: [{}]. Arguments: [{}].",
                        param, param.getConsumeMode(), JacksonUtils.objectToString(arguments));
                    return consume(storageCenter, supplier, param, attr, arguments);
                case SUCCESS:
                    return repeatConsume(param, attr, arguments);
                case CONSUMING:
                    if (retry) {
                        SleepUtils.sleep(attr.getTimeUnit(), attr.getExpectedCost());
                        return handleSuccessMode(param, attr, supplier, false, arguments);
                    } else {
                        String msg = String.format("[%s] has always been in consumption. Expected cost: [%s ms].", param, attr.getTimeUnit().toMillis(attr.getExpectedCost()));
                        IdempotentScenario scenario = attr.getScenario();
                        switch (scenario) {
                            case MQ:
                                throw new IdempotentException(msg);
                            case REST:
                                throw new ServiceException(msg);
                            default: // unreachable
                                return null;
                        }
                    }
                default: // unreachable
                    return null;
            }
        }
    }

    /**
     * 消费正常完成，将数据置为 {@link ConsumeStatus#SUCCESS}。 <br/>
     * 消费异常完成，将数据置为 {@link ConsumeStatus#EXCEPTION}。
     * <p/>
     * 对于消费异常完成。回调 {@link StorageCenter#exceptionCallback(IdempotentParamWrapper, IdempotentScenario, Object[], Throwable)}
     */
    private Object consume(StorageCenter storageCenter, TSupplier<Object> supplier,
                           IdempotentParamWrapper param, IdempotentAttr attr, Object[] arguments) throws Throwable {
        try {
            Object o = supplier.get();
            storageCenter.modifyStatus(param, ConsumeStatus.SUCCESS);
            return o;
        } catch (ServiceException e) {
            // MQ场景下，如果发生ServiceException，和其他异常一样处理
            // REST场景下，如果发生了ServiceException，不能阻塞用户的重试
            IdempotentScenario scenario = attr.getScenario();
            switch (scenario) {
                case MQ:
                    return storageCenter.exceptionCallback(param, scenario, arguments, e);
                case REST:
                    storageCenter.delete(param);
                    throw e;
                default: // unreachable
                    return null;
            }
        } catch (Exception e) {
            IdempotentScenario scenario = attr.getScenario();
            return storageCenter.exceptionCallback(param, scenario, arguments, e);
        }
    }

    /**
     * MQ：打印error日志。 <br/>
     * REST：抛ServiceException，通知用户。
     */
    private Object repeatConsume(IdempotentParamWrapper param, IdempotentAttr attr, Object[] arguments) {
        IdempotentScenario scenario = attr.getScenario();
        switch (scenario) {
            case MQ:
                log.error("[{}] has consumed. Mode: [{}]. Arguments: [{}].",
                    param, param.getConsumeMode(), JacksonUtils.objectToString(arguments));
                return null;
            case REST:
                String repeatConsumptionMsg = attr.getRepeatConsumptionMsg();
                Properties properties = new Properties();
                properties.put("prefix", attr.getPrefix());
                properties.put("spEL", attr.getSpEL());
                properties.put("scenario", attr.getScenario());
                properties.put("storageCenter", attr.getStorageCenter());
                properties.put("consumeMode", attr.getConsumeMode());
                properties.put("timeUnit", attr.getTimeUnit());
                properties.put("expectedCost", attr.getExpectedCost());
                properties.put("timeout", attr.getTimeout());
                properties.put("timeoutStr", attr.getTimeout() + " " + TimeUnitUtils.convertTimeUnit(attr.getTimeUnit()));
                properties.put("key", param.getKey());
                properties.put("simpleKey", param.getSimpleKey());
                properties.put("fullKey", param.getFullKey());
                throw new ServiceException(ValueResolverHelper.resolveHashPlaceholder(repeatConsumptionMsg, properties));
            default: // unreachable
                return null;
        }
    }

    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return this.environment.resolvePlaceholders(value);
        }
        return value;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.namespace = resolve(namespace);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
