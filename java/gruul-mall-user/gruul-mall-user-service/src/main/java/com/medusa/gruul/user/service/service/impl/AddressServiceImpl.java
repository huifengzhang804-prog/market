package com.medusa.gruul.user.service.service.impl;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.user.api.constant.UserConstant;
import com.medusa.gruul.user.api.model.UserAddressVO;
import com.medusa.gruul.user.service.model.dto.UserAddressDTO;
import com.medusa.gruul.user.service.mp.entity.UserAddress;
import com.medusa.gruul.user.service.mp.service.IUserAddressService;
import com.medusa.gruul.user.service.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户收货地址服务
 *
 * @author 张治保
 * date 2022/5/31
 */
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final IUserAddressService userAddressService;

    @Override
    public IPage<UserAddress> userAddressPage(Page<UserAddress> page) {
        return this.userAddressService.lambdaQuery()
                .eq(UserAddress::getUserId, ISecurity.userOpt().get().getId())
                .page(page);
    }

    @Override
    public UserAddressVO userAddressById(Long addressId) {
        UserAddress userAddress = this.userAddressService.lambdaQuery()
                .eq(UserAddress::getId, addressId)
                .eq(ISecurity.anyRole(Roles.USER), UserAddress::getUserId, ISecurity.userOpt().get().getId())
                .one();
        return userAddress == null ?
                null :
                new UserAddressVO()
                        .setId(userAddress.getId())
                        .setUserId(userAddress.getUserId())
                        .setName(userAddress.getName())
                        .setMobile(userAddress.getMobile())
                        .setLocation(userAddress.getLocation())
                        .setArea(userAddress.getArea())
                        .setAddress(userAddress.getAddress())
                        .setIsDefault(userAddress.getIsDefault());
    }

    @Override
    public UserAddress userAddressDefault() {
        return this.userAddressService.lambdaQuery()
                .eq(UserAddress::getUserId, ISecurity.userOpt().get().getId())
                .orderByDesc(ListUtil.of(UserAddress::getIsDefault, UserAddress::getUpdateTime, UserAddress::getCreateTime))
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
    }

    @Override
    @Redisson(value = UserConstant.USER_ADDRESS_UPDATE_LOCK, key = "#userId")
    public void newUserAddress(Long userId, UserAddressDTO userAddress) {
        boolean success = this.userAddressService.save(
                new UserAddress()
                        .setUserId(userId)
                        .setName(userAddress.getName())
                        .setMobile(userAddress.getMobile())
                        .setLocation(userAddress.getLocation())
                        .setArea(userAddress.getArea())
                        .setAddress(userAddress.getAddress())
                        .setIsDefault(
                                isAddressDefault(userAddress.getIsDefault(), userId)
                        )
        );
        if (!success) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED);
        }
    }

    /**
     * 新增地址时是否为默认地址
     *
     * @param isDefault 是否默认
     * @param userId    用户id
     * @return true 是 false 否
     */
    private Boolean isAddressDefault(Boolean isDefault, Long userId) {
        boolean exists = !this.userAddressService.lambdaQuery()
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, Boolean.TRUE)
                .exists();
        if (exists) {
            return Boolean.TRUE;
        }
        if (isDefault != null && isDefault) {
            boolean flag = this.userAddressService.lambdaUpdate()
                    .eq(UserAddress::getUserId, userId)
                    .set(UserAddress::getIsDefault, Boolean.FALSE)
                    .update();
            if (!flag) {
                throw new GlobalException(SystemCode.DATA_ADD_FAILED);
            }
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    @Redisson(value = UserConstant.USER_ADDRESS_UPDATE_LOCK, key = "#userId")
    public void editUserAddress(Long userId, Long addressId, UserAddressDTO userAddress) {
        boolean success = this.userAddressService.lambdaUpdate()
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getId, addressId)
                .set(UserAddress::getName, userAddress.getName())
                .set(UserAddress::getMobile, userAddress.getMobile())
                .set(UserAddress::getLocation, userAddress.getLocation())
                .set(UserAddress::getArea, JSON.toJSONString(userAddress.getArea()))
                .set(UserAddress::getAddress, userAddress.getAddress())
                .update();
        if (!success) {
            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = UserConstant.USER_ADDRESS_UPDATE_LOCK, key = "#userId")
    public void deleteUserAddress(Long userId, Long addressId) {

        UserAddress address = this.userAddressService.lambdaQuery()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, userId)
                .one();
        if (address == null) {
            return;
        }
        boolean success = this.userAddressService.lambdaUpdate()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, userId)
                .remove();
        if (!success) {
            throw new GlobalException(SystemCode.DATA_DELETE_FAILED);
        }
        //如果 当前地址不是默认地址 并且 用户没有其它收货地址 则 直接跳过
        if (!address.getIsDefault()) {
            return;
        }
        //否则 则随机把一个收货地址 设置成默认地址
        this.userAddressService.lambdaUpdate()
                .set(UserAddress::getIsDefault, Boolean.TRUE)
                .ne(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, userId)
                .last(SqlHelper.SQL_LIMIT_1)
                .update();
    }

    @Override
    @Redisson(value = UserConstant.USER_ADDRESS_UPDATE_LOCK, key = "#userId")
    public void updateUserAddressDefaultStatus(Long userId, Long addressId, Boolean isDefault) {
        boolean success = this.userAddressService.lambdaUpdate()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, ISecurity.userOpt().get().getId())
                .eq(UserAddress::getIsDefault, !isDefault)
                .set(UserAddress::getIsDefault, isDefault)
                .update();
        if (!success) {
            throw new GlobalException(SystemCode.DATA_DELETE_FAILED);
        }
        if (isDefault) {
            this.userAddressService.lambdaUpdate()
                    .eq(UserAddress::getUserId, ISecurity.userOpt().get().getId())
                    .ne(UserAddress::getId, addressId)
                    .eq(UserAddress::getIsDefault, Boolean.TRUE)
                    .set(UserAddress::getIsDefault, Boolean.FALSE)
                    .update();
        }
    }
}
