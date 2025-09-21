package com.medusa.gruul.addon.team.model;

import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/3/16
 */
@Getter
@Setter
@Accessors(chain = true)
public class OrderTeamCache implements Serializable {
    /**
     * 团号
     */
    private String teamNo;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 商品数量
     */
    private Integer productNum;

    /**
     * 选择的参团人数
     */
    private Integer userNum;

    /**
     * sku Key
     */
    private ShopProductSkuKey skuKey;

    /**
     * sku key 对应的价格
     */
    private Long skuPrice;


}
