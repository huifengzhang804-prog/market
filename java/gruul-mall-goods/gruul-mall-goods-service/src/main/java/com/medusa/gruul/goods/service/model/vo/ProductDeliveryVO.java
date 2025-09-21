package com.medusa.gruul.goods.service.model.vo;

import com.medusa.gruul.common.model.enums.DistributionMode;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wudi
 *  商品配送VO
 *
 */
@Data
@Accessors(chain = true)
public class ProductDeliveryVO {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * skuId
     */
    private Long skuId;

    /**
     * 配送方式(0--商家配送，1--快递配送，2--同城配送,3--门店)
     */
    private List<DistributionMode> distributionMode;
    /**
     * 运费模板ID
     */
    private Long freightTemplateId;
    /**
     * 商品重量，默认为克
     */
    private BigDecimal weight;


}
