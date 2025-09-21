-- `KEYS` 包含哈希表的键
-- `ARGV` 包含每个哈希表的字段和值，格式为：
-- field1, value1, field2, value2, ..., key2_field1, key2_value1, ...

-- 当前处理的字段和值对的索引 
local arg_index = 1
-- 下一个键的标识 如果当前键的字段和值对处理完了，就会遇到这个标识
local next_flag = '__NeXt__KeY__'

for i = 1, #KEYS do
    local key = KEYS[i]

    -- 检查键是否存在
    if redis.call('exists', key) == 1 then
        -- 当当前键的字段和值对还未处理完时
        while arg_index <= #ARGV do
            local field = ARGV[arg_index]
            -- 移除字符串的前后双引号
            field = string.sub(field, 2, -2)
            -- 检查是否到了下一个键的字段和值对
            if field == next_flag then
                arg_index = arg_index + 1
                break
            end
            -- 设置哈希表的字段和值
            redis.call('hset', key, field, ARGV[arg_index + 1])

            -- 移动到下一个字段和值对
            arg_index = arg_index + 2
        end
    else
        -- 如果键不存在，跳过当前键的字段和值对
        while arg_index <= #ARGV and ARGV[arg_index] ~= next_flag do
            arg_index = arg_index + 2
        end
        arg_index = arg_index + 1
    end
end
