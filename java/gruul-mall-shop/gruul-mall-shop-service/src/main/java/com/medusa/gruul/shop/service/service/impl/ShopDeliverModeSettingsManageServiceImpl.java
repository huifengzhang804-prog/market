package com.medusa.gruul.shop.service.service.impl;

import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.shop.api.enums.ShopDeliverModeSettingsEnum;
import com.medusa.gruul.shop.api.model.vo.ShopDeliverModeSettings;
import com.medusa.gruul.shop.service.model.ShopConstants;
import com.medusa.gruul.shop.service.service.ShopDeliverModeSettingsManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author: wufuzhong
 * @Date: 2023/11/07 10:14:08
 * @Description: 自营店铺发货方式配置service实现
 */

@Service
@RequiredArgsConstructor
public class ShopDeliverModeSettingsManageServiceImpl implements ShopDeliverModeSettingsManageService {


    /**
     * 设置或更新自营店铺发货方式配置
     *
     * @param param 入参
     */
    @Override
    public void saveOrUpdate(ShopDeliverModeSettings param) {
        RedisUtil.setCacheMap(
                ShopConstants.PLATFORM_DELIVER_MODE_SETTINGS,
                new com.medusa.gruul.shop.api.model.vo.ShopDeliverModeSettings()
                        .setShopDeliver(param.getShopDeliver())
                        .setSupplierDeliver(param.getSupplierDeliver())
        );
    }

    /**
     * 获取自营店铺发货方式配置
     *
     * @return ShopDeliverModeSettingsVO 返回发货方式配置对象
     */
    @Override
    public ShopDeliverModeSettings getShopDeliverModeSettings() {
        ShopDeliverModeSettings cache = RedisUtil.getCacheMap(
                ShopConstants.PLATFORM_DELIVER_MODE_SETTINGS,
                ShopDeliverModeSettings.class
        );
        if (cache == null) {
            cache = new ShopDeliverModeSettings()
                    .setShopDeliver(ShopDeliverModeSettingsEnum.OWN)
                    .setSupplierDeliver(ShopDeliverModeSettingsEnum.OWN);
        }
        return cache;
    }
}
