package com.medusa.gruul.shop.service.service;


import com.medusa.gruul.shop.api.model.vo.ShopDeliverModeSettings;

/**
 * @author: wufuzhong
 * @Date: 2023/11/07 09:29:05
 * @Description: 自营店铺发货方式配置service
 */
public interface ShopDeliverModeSettingsManageService {

    /**
     * 设置或更新自营店铺发货方式配置
     *
     * @param param 入参
     */
    void saveOrUpdate(ShopDeliverModeSettings param);

    /**
     * 获取自营店铺发货方式配置
     *
     * @return ShopDeliverModeSettingsVO 返回发货方式配置对象
     */
    ShopDeliverModeSettings getShopDeliverModeSettings();
}
