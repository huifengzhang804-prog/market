package com.medusa.gruul.overview.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 畅销店铺VO
 *
 * @author xiaoq
 * @Description SalableShopVO.java
 * @date 2022-10-21 14:57
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SalableShopVO {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;
    /**
     * 店铺Logo
     */
    private String shopLogo;

    /**
     * 店铺营业额
     */
    private Long realTradingVolume;


}
