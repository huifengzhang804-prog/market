package com.medusa.gruul.addon.bargain;

import com.medusa.gruul.common.redis.util.RedisUtil;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BargainApplicationTests {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate = RedisUtil.getRedisTemplate();

    @org.junit.Test
    public void test() {
        String key = "test";
        Integer[] arr = new Integer[]{1, 2, 3, 4, 5};
        redisTemplate.opsForList().leftPushAll(key, arr);
        System.out.println("Hello World!");
    }

}
