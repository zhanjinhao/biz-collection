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
     * 删除消费记录
     */
    void delete(IdempotentParamWrapper paramWrapper);

    /**
     * 消费异常（ServiceException不属于异常，属于中断操作）时的回调方法。
     * 消费异常有两种情况：
     * <ul><li>
     * 一是数据被标记为{@link ConsumeStatus#CONSUMING}失败，此时是{@link StorageCenter#saveIfAbsent(IdempotentParamWrapper, ConsumeStatus)} 执行失败导致。
     * 由于此时未进入真正的消费代码，所以对MQ场景必须进行打日志/扔私信队列等操作，避免消息丢失。
     * </li>
     * <li>
     * 二是数据被标记为{@link ConsumeStatus#CONSUMING}成功，此时是业务代码执行失败导致。所以一定要将消息标记为{@link ConsumeStatus#EXCEPTION}，避免影响后续同消息的重试。
     * </li><ul/>
     */
    Object exceptionCallback(IdempotentParamWrapper paramWrapper, IdempotentScenario scenario, Object[] arguments, Throwable e) throws Throwable;

}
