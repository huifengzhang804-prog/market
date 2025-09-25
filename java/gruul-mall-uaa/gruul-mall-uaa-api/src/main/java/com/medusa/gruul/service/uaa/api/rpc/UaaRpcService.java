package com.medusa.gruul.service.uaa.api.rpc;

import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.service.uaa.api.enums.SmsType;
import com.medusa.gruul.service.uaa.api.vo.ShopUserInfoVO;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/9/23
 */
public interface UaaRpcService {

    /**
     * 根据用户id查询用户资料
     *
     * @param userId 用户ID
     * @return 用户资料 option
     */
    Option<UserInfoVO> getUserDataByUserId(@NotNull Long userId);

    /**
     * 批量查询用户数据
     *
     * @param userIds 用户id集合
     * @return 用户id与用户资料映射
     */
    Map<Long, UserInfoVO> getUserDataBatchByUserIds(@NotNull @Size(min = 1) Set<Long> userIds);

    /**
     * 根据店铺id 用户id 查询店铺用户资料
     *
     * @param shopId  店铺id
     * @param adminId 店铺用户id
     * @return 店铺用户资料 option
     */
    Option<ShopUserInfoVO> getShopUserDataByShopIdUserId(@NotNull Long shopId, @NotNull Long adminId);

    /**
     * 查询店铺用户资料 与 指定用户资料
     *
     * @param shopId  店铺id
     * @param adminId 店铺用户id
     * @param userId  指定用户id
     * @return 查询店铺用户资料 与 指定用户资料 元组
     */
    Tuple2<Option<ShopUserInfoVO>, Option<UserInfoVO>> getShopUserDataAndUserData(@NotNull Long shopId, @NotNull Long adminId, @NotNull Long userId);


    /**
     * 验证码校验 by smsType
     *
     * @param smsType   短信类型
     * @param mobile    手机号
     * @param inputCode 输入的验证码
     */
    void checkSmsCodeByType(SmsType smsType, String mobile, String inputCode);

    /**
     * 检测门店用户
     *
     * @param mobiles 门店用户手机号
     */
    void checkStoreUserByMobile(List<String> mobiles);

    /**
     * 店铺添加主播角色
     *
     * @param userId 用户id
     */
    void addAnchorAuthority(Long userId);


    /**
     * 删除用户角色权限根据手机号
     *
     * @param mobile 用户手机号
     * @param roles  角色
     * @return 是否成功
     */
    boolean delUserRoleByMobiles(String mobile, Roles roles);

    String getVerifyCode(String mobile);
}
