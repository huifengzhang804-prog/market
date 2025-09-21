package com.medusa.gruul.user.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.order.api.model.OrderCreatedDTO;
import com.medusa.gruul.user.service.model.dto.ShopUserQueryDTO;
import com.medusa.gruul.user.service.model.vo.ShopUserVO;

/**
 * @author wudi
 * ShopUserAccountManageService.class
 */
public interface ShopUserAccountManageService {

    /**
     * 分页查询店铺客户列表
     *
     * @param shopUserQuery 店铺客户查询参数
     * @return 店铺客户列表
     */
    IPage<ShopUserVO> getShopUserAccountList(ShopUserQueryDTO shopUserQuery);
    /**
     * 获取店铺客户详情
     *
     * @param userId 店铺客户id
     * @return 店铺客户详情
     */
    ShopUserVO getShopUserAccountDetail(Long userId);

    /**
     * 用户提交订单添加店铺客户
     * @param shopUserAccount 店铺客户
     */
    void addShopUserAccount(OrderCreatedDTO shopUserAccount);
}
