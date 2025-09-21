package com.medusa.gruul.addon.bargain.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 砍价商品详情VO 替换map
 *
 * @author wufuzhong
 * @since 2024/2/26
 */
@Getter
@Setter
public class BargainSponsorProductVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 砍价商品详情 列表
     */
    private List<BargainProductDetailVO> bargainProductDetails;

    /**
     * 砍价发起人商品sku信息
     */
    private BargainSponsorProductSkuVO sponsorProductSku;
}
