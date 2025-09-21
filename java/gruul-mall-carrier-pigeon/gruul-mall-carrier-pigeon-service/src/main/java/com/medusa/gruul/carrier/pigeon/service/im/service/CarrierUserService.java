package com.medusa.gruul.carrier.pigeon.service.im.service;

import com.medusa.gruul.carrier.pigeon.service.im.entity.UserInfo;
import com.medusa.gruul.service.uaa.api.dto.UserBaseDataDTO;

import java.util.Map;
import java.util.Set;

/**
 * <p>用户接口</p>
 *
 * @author An.Yan
 */
public interface CarrierUserService {
    /**
     * 尝试从Redis中获取用户信息,若不存在则从RPC拉取并缓存到Redis.
     *
     * @param userId 用户ID
     * @return {@link UserInfo}
     */
    UserInfo checkAndGetUserInfo(Long userId);

    /**
     * 更新Redis用户信息
     *
     * @param userData 用户资料
     */
    void updateUserInfo(UserBaseDataDTO userData);

    /**
     * 方法 checkUserRights
     * @param userIds 用户ID的集合，其权限需要被检查
     * @return 一个Map，其中键是用户ID，值是一个布尔值(是否包含专属会员)，表示该用户是否具有权限
     */
    Map<Long, Boolean> checkUserRights(Set<Long> userIds);



}
