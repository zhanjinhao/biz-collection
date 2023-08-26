local stockKey = ARGV[1]
local userId = ARGV[2]
local orderKey = ARGV[3]

-- 3.1.判断用户是否下单 SISMEMBER orderKey userId
if (redis.call('sismember', orderKey, userId) == 1) then
    -- 存在，说明是重复下单，返回1
    return 1
end

-- 3.2.判断库存是否充足 get stockKey
if (tonumber(redis.call('get', stockKey)) <= 0) then
    -- 库存不足，返回1
    return 2
end

-- 3.4.扣库存 incrby stockKey -1
redis.call('incrby', stockKey, -1)
-- 3.5.下单（保存用户）sadd orderKey userId
redis.call('sadd', orderKey, userId)
-- 3.6.发送消息到队列中， XADD stream.orders * k1 v1 k2 v2 ...
--redis.call('xadd', 'stream.seckillOrders', '*', 'userId', userId, 'seckillGoodsId', seckillGoodsId)
return 0