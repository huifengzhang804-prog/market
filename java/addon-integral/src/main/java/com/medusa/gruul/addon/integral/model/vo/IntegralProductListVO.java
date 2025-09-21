package com.medusa.gruul.addon.integral.model.vo;

import com.medusa.gruul.addon.integral.model.enums.IntegralProductStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 积分商品VO
 *
 * @author xiaoq
 * @Description 积分商品VO
 * @date 2023-01-31 16:39
 */
@Getter
@Setter
@ToString
public class IntegralProductListVO {

    private Long id;

    /**
     * 积分商品名称
     */
    private String name;

    /**
     * 市场价格
     */
    private BigDecimal price;

    /**
     * 积分价
     */
    private Long integralPrice;

    /**
     * 混合支付金额
     */
    private Long salePrice;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 展示主图
     */
    private String pic;

    /**
     * 积分商品状态
     */
    private IntegralProductStatus status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 销量
     */
    private Long salesVolume;

    /**
     * 商品类型
     */
    private ProductType productType;

}
