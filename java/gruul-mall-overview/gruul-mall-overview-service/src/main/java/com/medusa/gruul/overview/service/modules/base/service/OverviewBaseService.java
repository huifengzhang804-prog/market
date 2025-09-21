package com.medusa.gruul.overview.service.modules.base.service;

import com.medusa.gruul.overview.service.mp.base.entity.OverviewShop;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import io.vavr.control.Option;

/**
 * @author 张治保
 * date 2022/11/23
 */
public interface OverviewBaseService {

    /**
     * 根据店铺id查询店铺信息
     *
     * @param shopId 店铺id
     * @return 店铺信息可能为空
     */
    Option<OverviewShop> getShopById(Long shopId);

    /**
     * 检查是否存在店铺信息 不存在则rpc查询 落库
     *
     * @param shopId 店铺id
     */
    void checkOrSaveShop(Long shopId);

    /**
     * 更新店铺信息
     *
     * @param shopInfo 店铺信息
     */
    void updateShopInfo(ShopInfoVO shopInfo);

    /**
     * 检查店铺信息是否存在 不存在则保存店铺信息
     *
     * @param shopId 店铺id
     * @param name   店铺名称
     * @param logo   店铺logo
     */
    void checkOrSaveShop(Long shopId, String name, String logo);


    /**
     * 保存店铺信息
     *
     * @param shopId   店铺id
     * @param shopName 店铺名称
     * @param shopLogo 店铺logo
     * @return 店铺信息
     */
    OverviewShop saveShop(Long shopId, String shopName, String shopLogo);




    /**
     * 保存 用户端提现人信息
     *
     * @param userId 用户id
     * @param name 用户姓名
     * @param avatar 用户头像
     */
    void checkOrSaveConsumer(Long userId, String name, String avatar);


}
