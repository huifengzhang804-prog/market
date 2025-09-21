package com.medusa.gruul.service.uaa.service.service.rpc;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.service.uaa.api.enums.SmsType;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.ShopUserInfoVO;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import com.medusa.gruul.service.uaa.service.model.vo.UserWithDataVO;
import com.medusa.gruul.service.uaa.service.mp.entity.ShopUserData;
import com.medusa.gruul.service.uaa.service.mp.service.IShopUserDataService;
import com.medusa.gruul.service.uaa.service.mp.service.IUserService;
import com.medusa.gruul.service.uaa.service.service.LiveAnchorService;
import com.medusa.gruul.service.uaa.service.service.SmsService;
import com.medusa.gruul.service.uaa.service.service.StoreUserService;
import com.medusa.gruul.service.uaa.service.service.UserRoleCancelService;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/9/23
 */
@Service
@DubboService
@RequiredArgsConstructor
public class UaaRpcServiceImpl implements UaaRpcService {

    private final SmsService smsService;
    private final IUserService userService;
    private final StoreUserService storeUserService;
    private final LiveAnchorService liveAnchorService;
    private final IShopUserDataService shopUserDataService;
    private final UserRoleCancelService userRoleCancelService;


    @Override
    public Option<UserInfoVO> getUserDataByUserId(Long userId) {
        return Option.of(
                this.getUserDataBatchByUserIds(Set.of(userId)).get(userId)
        );
    }

    @Override
    public Map<Long, UserInfoVO> getUserDataBatchByUserIds(Set<Long> userIds) {
        List<UserWithDataVO> userDatas = userService.getUserDataBatchByUserId(userIds, Boolean.FALSE);
        if (CollUtil.isEmpty(userDatas)) {
            return Map.of();
        }
        return userDatas.stream()
                .collect(
                        Collectors.toMap(
                                UserWithDataVO::getUserId,
                                userWithData -> new UserInfoVO()
                                        .setUserId(userWithData.getUserId())
                                        .setOpenid(userWithData.getOpenid())
                                        .setUsername(userWithData.getUsername())
                                        .setMobile(userWithData.getMobile())
                                        .setEmail(userWithData.getEmail())
                                        .setNickname(userWithData.getNickname())
                                        .setAvatar(userWithData.getAvatar())
                                        .setGender(userWithData.getGender())
                        )
                );
    }

    @Override
    public Option<ShopUserInfoVO> getShopUserDataByShopIdUserId(Long shopId, Long adminId) {
        return Option.of(
                        ISystem.shopId(
                                shopId,
                                () -> shopUserDataService.lambdaQuery().eq(ShopUserData::getUserId, adminId).one()
                        )
                )
                .map(
                        shopUserData -> new ShopUserInfoVO()
                                .setShopId(shopId)
                                .setUserId(adminId)
                                .setNickname(shopUserData.getNickname())
                                .setEmail(shopUserData.getEmail())
                                .setMobile(shopUserData.getMobile())
                );
    }

    @Override
    public Tuple2<Option<ShopUserInfoVO>, Option<UserInfoVO>> getShopUserDataAndUserData(Long shopId, Long adminId, Long userId) {
        return Tuple.of(
                getShopUserDataByShopIdUserId(shopId, adminId),
                getUserDataByUserId(userId)
        );
    }

    /**
     * 短信校验 by smsType
     *
     * @param smsType   短信类型
     * @param mobile    手机号
     * @param inputCode 输入的验证码
     */
    @Override
    public void checkSmsCodeByType(SmsType smsType, String mobile, String inputCode) {
        smsService.checkSmsCode(smsType, mobile, inputCode);
    }

    /**
     * 检测门店用户 (新用户|旧用户)/赋值门店角色
     *
     * @param mobiles 门店用户手机号
     */
    @Override
    public void checkStoreUserByMobile(List<String> mobiles) {
        storeUserService.checkStoreUserByMobile(mobiles);

    }

    /**
     * 添加主播角色
     *
     * @param userId 手机号
     */
    @Override
    public void addAnchorAuthority(Long userId) {
        liveAnchorService.addAnchorAuthority(userId);
    }

    /**
     * 删除用户角色权限根据手机号
     *
     * @param mobile 用户手机号
     * @param roles  角色
     * @return 是否成功
     */
    @Override
    public boolean delUserRoleByMobiles(String mobile, Roles roles) {
        return userRoleCancelService.delUserRoleByMobiles(mobile, roles);
    }
}
