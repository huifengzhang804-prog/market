package com.medusa.gruul.addon.ic.modules.ic.model.vo;

import com.medusa.gruul.addon.ic.modules.ic.model.dto.ICShopConfigDTO;
import com.medusa.gruul.addon.ic.modules.ic.mp.entity.ICShopConfig;
import com.medusa.gruul.shop.api.model.vo.ShopAddressVO;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2024/8/23
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ShopConfigVO extends ICShopConfigDTO {
    /**
     * 定位
     */
    private Point location;

    /**
     * 详细地址
     */
    private String address;

//    /**
//     * 是否支持同城配送
//     */
//    private Boolean icStatus = Boolean.FALSE;


    public static ShopConfigVO of(ShopAddressVO shopAddress, ICShopConfig config) {
        ShopConfigVO result = new ShopConfigVO();
        result.setEnableSelf(config.getEnableSelf())
                .setEnableOpen(config.getEnableOpen())
                .setDefaultType(config.getDefaultType())
                .setWarmBox(config.getWarmBox())
                .setDeliveryRange(config.getDeliveryRange())
                .setDescription(config.getDescription())
                .setMinLimit(config.getMinLimit())
                .setBaseDelivery(config.getBaseDelivery())
                .setBillMethod(config.getBillMethod())
                .setFreeLimit(config.getFreeLimit());
        result.setLocation(shopAddress.getLocation());
        result.setAddress(shopAddress.fullAddress());
        result.setIcStatus(config.getIcStatus());
        return result;
    }

}
