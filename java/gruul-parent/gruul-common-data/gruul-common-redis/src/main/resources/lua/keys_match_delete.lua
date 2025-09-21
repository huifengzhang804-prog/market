-- 批量匹配键 并删除数据
-- 每次扫描的大小
local scanSize = tonumber(ARGV[1]);
-- 遍历所有的匹配模式
for i = 1, #KEYS do
    local keyPattern = KEYS[i];
    local cursor = "0";
    repeat
        local result = redis.call("SCAN", cursor, "MATCH", keyPattern, "COUNT", scanSize);
        cursor = result[1];
        local keys = result[2];
        -- 将匹配到的键添加到 keysCollector 中
        -- 合并数组
        if #keys ~= 0 then
            redis.call('del', unpack(keys));
        end
    until (cursor == "0");
end
