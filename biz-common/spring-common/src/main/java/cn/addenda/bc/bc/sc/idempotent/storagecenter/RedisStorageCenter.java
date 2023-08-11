package cn.addenda.bc.bc.sc.idempotent.storagecenter;

import cn.addenda.bc.bc.jc.util.JacksonUtils;
import cn.addenda.bc.bc.sc.idempotent.ConsumeStatus;
import cn.addenda.bc.bc.sc.idempotent.IdempotentScenario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author addenda
 * @since 2023/7/29 15:18
 */
@Slf4j
public class RedisStorageCenter implements StorageCenter {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisStorageCenter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public ConsumeStatus get(IdempotentParamWrapper param) {
        return ConsumeStatus.valueOf(stringRedisTemplate.opsForValue().get(param.getFullKey()));
    }

    @Override
    public boolean saveIfAbsent(IdempotentParamWrapper param, ConsumeStatus consumeStatus) {
        return Boolean.TRUE.equals(
                stringRedisTemplate.opsForValue().setIfAbsent(param.getFullKey(),
                        consumeStatus.name(), param.getTimeoutSecs(), TimeUnit.SECONDS));
    }

    @Override
    public void modifyStatus(IdempotentParamWrapper param, ConsumeStatus consumeStatus) {
        stringRedisTemplate.opsForValue().set(
                param.getFullKey(), consumeStatus.name(), param.getTimeoutSecs(), TimeUnit.SECONDS);
    }

    /**
     * MQ：打印error日志。参数不存Redis，因为redis用内存存储数据，容易打满进而影响到其他业务。 <br/>
     * REST：异常向上抛出。
     */
    @Override
    public Object exceptionCallback(IdempotentParamWrapper param, IdempotentScenario scenario,
                                    Object[] arguments, Throwable e) throws Throwable {
        switch (scenario) {
            case MQ:
                log.error("[{}] Consume error. Mode: [{}]. Arguments: [{}].",
                        param, param.getConsumeMode(), JacksonUtils.objectToString(arguments));
                return null;
            case REST:
                throw e;
            default: // unreachable
                return null;
        }
    }

}
