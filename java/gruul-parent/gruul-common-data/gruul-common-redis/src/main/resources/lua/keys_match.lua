-- 批量匹配键

-- 匹配到的所有键 集合
local keysCollector = {};
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
        if (keys ~= nil) then
            for _, key in ipairs(keys) do
                table.insert(keysCollector, key)
            end
        end
    until (cursor == "0");
end
return keysCollector;