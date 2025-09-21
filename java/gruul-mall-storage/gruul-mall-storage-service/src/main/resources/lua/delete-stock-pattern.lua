local keys = redis.call('keys', ARGV[1])
for i, k in ipairs(keys) do
    redis.call('del', k)
end
return 0