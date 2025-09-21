package com.medusa.gruul.goods.api.model.vo;

import com.medusa.gruul.storage.api.vo.ProductSaleVolumeVO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * pc端关注店铺和商品
 * @author WuDi
 * date: 2022/11/8
 */
@Data
@Accessors(chain = true)
public class ApiFollowShopProductVO {
    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺logo
     */
    private String shopLogo;

    /**
     * 店铺的商品
     */
    private List<ProductSaleVolumeVO> productSaleVolumeVOList;

}
