package com.medusa.gruul.addon.rebate.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.medusa.gruul.addon.rebate.model.enums.SettlementStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RebateOrderItem {

    /**
     * 订单项id
     */
    private Long orderItemId;


    /**
     * 所属订单号
     */
    private String orderNo;

    /**
     * 店铺 id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品类型
     */
    private ProductType productType;

    /**
     * 商品 sku Id
     */
    private Long skuId;

    /**
     * 商品规格
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> specs;

    /**
     * 购买数量
     */
    private Integer num;

    /**
     * 该商品sku 图片
     */
    private String image;

    /**
     * 销售单价
     */
    private Long salePrice;

    /**
     * 成交价
     */
    private Long dealPrice;

    /**
     * 平台服务费
     */
    private Long platformServiceFee;

    /**
     * 返利计算
     */
    private String rebateCalculation;

    /**
     * 结算状态
     */
    private SettlementStatus settlementStatus;

    /**
     * 预计返利
     */
    private Long estimatedRebate;

    /**
     * 返利合计
     */
    private Long totalRebate;
}
