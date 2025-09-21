package com.medusa.gruul.shop.api.model.vo;

import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.shop.api.enums.ShopDeliverModeSettingsEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author: wufuzhong
 * @Date: 2023/11/07 09:40:49
 * @Description: 自营店铺发货设置 VO
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopDeliverModeSettings implements BaseDTO {

    /**
     * 自营店铺发货方式
     */
    private ShopDeliverModeSettingsEnum shopDeliver;

    /**
     * 自营供应商发货方式
     */
    private ShopDeliverModeSettingsEnum supplierDeliver;
}
