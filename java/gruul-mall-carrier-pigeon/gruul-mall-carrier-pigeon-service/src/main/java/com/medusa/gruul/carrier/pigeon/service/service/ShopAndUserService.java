package com.medusa.gruul.carrier.pigeon.service.service;

import com.medusa.gruul.carrier.pigeon.service.mp.entity.MessageShop;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.MessageShopAdmin;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.MessageUser;
import io.vavr.control.Option;

/**
 * @author 张治保
 * date 2022/10/12
 */
public interface ShopAndUserService {


    /**
     * 检查并获取 店铺信息 店铺信息不存在 则 新增店铺信息
     *
     * @param shopId 店铺id
     * @return 店铺信息
     */
    MessageShop checkAndGetMessageShop(Long shopId);

    /**
     * 检查店铺管理员信息是否存在，不存在 则生成店铺管理员信息
     *
     * @param shopId  店铺id
     * @param adminId 管理员用户id
     * @return 店铺管理员信息
     */
    MessageShopAdmin checkAndGetMessageShopAdmin(Long shopId, Long adminId);

    /**
     * 检查用户信息是否存在 不存在则生成用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    MessageUser checkAndGetMessageUser(Long userId);


    /**
     * 获取用户最后一条与该店铺聊天的消息 （查询一天内的消息）
     *
     * @param shopId 店铺id
     * @param userId 用户id
     * @return 可能为null的店铺管理员id
     */
    Option<Long> getLastMessageAdminId(Long shopId, Long userId);

}
