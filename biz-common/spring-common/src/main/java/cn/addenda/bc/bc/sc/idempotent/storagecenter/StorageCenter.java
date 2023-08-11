package cn.addenda.bc.bc.sc.idempotent.storagecenter;

import cn.addenda.bc.bc.sc.idempotent.ConsumeStatus;
import cn.addenda.bc.bc.sc.idempotent.IdempotentScenario;

/**
 * @author addenda
 * @since 2023/7/29 15:10
 */
public interface StorageCenter {

    ConsumeStatus get(IdempotentParamWrapper paramWrapper);

    /**
     * 数据ID存起来。原子操作。
     */
    boolean saveIfAbsent(IdempotentParamWrapper paramWrapper, ConsumeStatus consumeStatus);

    /**
     * 消费完之后，将数据ID打上标志
     */
    void modifyStatus(IdempotentParamWrapper paramWrapper, ConsumeStatus consumeStatus);

    /**
     * 消费异常时的回调方法
     */
    Object exceptionCallback(IdempotentParamWrapper paramWrapper, IdempotentScenario scenario, Object[] arguments, Throwable e) throws Throwable;

}
