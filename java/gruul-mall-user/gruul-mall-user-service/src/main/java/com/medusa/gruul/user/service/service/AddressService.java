package com.medusa.gruul.user.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.user.api.model.UserAddressVO;
import com.medusa.gruul.user.service.model.dto.UserAddressDTO;
import com.medusa.gruul.user.service.mp.entity.UserAddress;

/**
 * @author 张治保
 * date 2022/5/31
 */
public interface AddressService {

    /**
     * 分页查询用户收货地址
     *
     * @param page 分页参数
     * @return 查询结果
     */
    IPage<UserAddress> userAddressPage(Page<UserAddress> page);

    /**
     * 根据地址id查询 收货地址详情
     *
     * @param addressId 收货地址id
     * @return 收货地址详情
     */
    UserAddressVO userAddressById(Long addressId);

    /**
     * 获取默认的收货地址 没有默认则根据创建时间倒叙排序
     *
     * @return 默认收货地址
     */
    UserAddress userAddressDefault();

    /**
     * 新增用户地址
     *
     * @param userId      用户id
     * @param userAddress 收货地址详情
     */
    void newUserAddress(Long userId, UserAddressDTO userAddress);

    /**
     * 修改收货地址
     *
     * @param userId      用户id
     * @param addressId   地址id
     * @param userAddress 收货地址详情
     */
    void editUserAddress(Long userId, Long addressId, UserAddressDTO userAddress);

    /**
     * 删除用户收货地址
     *
     * @param addressId 地址id
     * @param userId 用户id
     */
    void deleteUserAddress(Long userId, Long addressId);

    /**
     * 更新收货地址是否默认状态
     *
     * @param userId    用户id
     * @param addressId 收货地址id
     * @param isDefault 是否是默认
     */
    void updateUserAddressDefaultStatus(Long userId, Long addressId, Boolean isDefault);


}
