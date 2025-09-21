package com.medusa.gruul.carrier.pigeon.service.im.service.impl;

import com.medusa.gruul.carrier.pigeon.service.im.entity.UserInfo;
import com.medusa.gruul.carrier.pigeon.service.im.repository.UserInfoRepository;
import com.medusa.gruul.carrier.pigeon.service.im.service.CarrierUserService;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.service.uaa.api.dto.UserBaseDataDTO;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.user.api.enums.RightsType;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * <p>信鸽用户接口实现</p>
 *
 * @author An.Yan
 */
@Service
@RequiredArgsConstructor
public class CarrierUserServiceImpl implements CarrierUserService {

    private final UserInfoRepository userInfoRepository;
    private final UaaRpcService uaaRpcService;
    private final UserRpcService userRpcService;

    /**
     * 尝试从Redis中获取用户信息,若不存在则从RPC拉取并缓存到Redis.
     *
     * @param userId 用户ID
     * @return {@link UserInfo}
     */
    @Override
    public UserInfo checkAndGetUserInfo(Long userId) {
        return Option
                .of(userInfoRepository.getUserById(userId))
                .getOrElse(
                        () ->
                                uaaRpcService.getUserDataByUserId(userId)
                                        .map(
                                                user -> {
                                                    UserInfo userInfo = new UserInfo(userId, user.getNickname(), user.getAvatar());
                                                    userInfoRepository.saveUser(new UserInfo(userId, user.getNickname(), user.getAvatar()));
                                                    return userInfo;
                                                }
                                        )
                                        .getOrElseThrow(() -> new GlobalException("用户不存在"))
                );
    }

    /**
     * 更新Redis用户信息
     *
     * @param userData 用户资料
     */
    @Override
    public void updateUserInfo(UserBaseDataDTO userData) {
        if (!userInfoRepository.userExists(userData.getUserId())) {
            return;
        }
        userInfoRepository.saveUser(
                new UserInfo(userData.getUserId(), userData.getNickname(), userData.getAvatar())
        );
    }

    @Override
    public Map<Long, Boolean> checkUserRights(Set<Long> userIds) {
        return userRpcService.getUserRights(userIds, RightsType.EXCLUSIVE_SERVICE);
    }
}
