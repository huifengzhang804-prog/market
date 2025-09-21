package com.medusa.gruul.user.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.user.service.model.dto.ShopUserQueryDTO;
import com.medusa.gruul.user.service.model.vo.ShopUserVO;
import com.medusa.gruul.user.service.mp.entity.ShopUserAccount;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *  服务类
 *
 * @author WuDi
 * @since 2023-05-17
 */
public interface IShopUserAccountService extends IService<ShopUserAccount> {

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
     * @param shopId 店铺id
     * @param userId 店铺客户id
     * @return 店铺客户详情
     */
    ShopUserVO getShopUserAccountDetail(Long shopId, Long userId);
}
