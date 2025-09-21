package com.medusa.gruul.carrier.pigeon.service.im.repository;

import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.carrier.pigeon.service.im.constants.IMRedisConstant;
import com.medusa.gruul.carrier.pigeon.service.im.entity.UserInfo;
import com.medusa.gruul.common.redis.util.RedisUtil;
import org.springframework.stereotype.Repository;

/**
 * 基于Redis实现的用户信息持久层
 *
 * @author An.Yan
 */
@Repository
public class UserInfoRepository {

    /**
     * 保存用户信息到Redis
     *
     * @param userInfo 用户信息,参考 {@link UserInfo}
     */
    public void saveUser(UserInfo userInfo) {
        RedisUtil.getRedisTemplate().opsForHash().put(
                IMRedisConstant.IM_USERINFO_KEY,
                userInfo.getUserKey(), JSON.toJSONString(userInfo));
    }

    /**
     * 根据ID获取用户信息
     *
     * @param userId 用户ID
     * @return {@link UserInfo}
     */
    public UserInfo getUserById(Long userId) {
        return RedisUtil.getCacheMapValue(
                IMRedisConstant.IM_USERINFO_KEY,
                userId.toString(),
                UserInfo.class
        );
    }

    /**
     * 判断用户资料存不存在
     *
     * @param userId 用户ID
     * @return 是否存在
     */
    public boolean userExists(Long userId) {
        return RedisUtil.getRedisTemplate().opsForHash().hasKey(IMRedisConstant.IM_USERINFO_KEY, String.valueOf(userId));
    }
}
