-- KEYS[1]  商品ID
-- ARGV[n]  库存与销量对象
-- 统计库存扣减次数
local total = 0
local ignoreNotExists = ARGV[1] == 'true'
for i, key in ipairs(KEYS) do
    -- 当前库存信息
    local stockInfo = redis.call('hmget', key, 'stock', 'stockType')
    local curStock = stockInfo[1]
    -- 如果当前库存不为空
    if curStock ~= nil then
        curStock = tonumber(curStock)
        -- 库存数量 todo 可能不需要 手动转换
        -- local curStock = tonumber(stockStr)
        -- 当前对应参数 库存 与 销量对象
        local stSv = cjson.decode(ARGV[i + 1])
        -- 库存 
        local stock = tonumber(stSv.stock)
        -- 如果库存不为0 则更新库存
        if stock ~= 0 then
            -- 当前库存类型 LIMITED 限量库存 UNLIMITED 无限库存
            local curStockType = stockInfo[2]
            -- 如果当前的库存类型 是限量库存 则尝试扣减库存
            if curStockType == '"LIMITED"' then
                -- 如果当前库存小于扣减库存 则回滚
                if curStock < (-1 * stock) then
                    -- 库存回滚
                    return total
                end
                -- 无限库存不需要操作库存 限量库存需要操作库存
                redis.call('hincrby', key, 'stock', stock)
            end
        end
        -- 销量
        local sales = tonumber(stSv.sales)
        if sales ~= 0 then
            redis.call('hincrby', key, 'salesVolume', sales)
        end
    elseif not ignoreNotExists then
        -- 如果当前库存不存在 则跳过
        -- 如果不忽略不存在的key 则直接返回 由程序回滚数据
        return total
    end
    -- 成功次数+1 
    total = total + 1
end
return total
