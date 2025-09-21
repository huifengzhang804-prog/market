package com.medusa.gruul.addon.rebate.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.medusa.gruul.addon.rebate.model.enums.RebateOrderStatus;
import com.medusa.gruul.addon.rebate.model.enums.SettlementStatus;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 返利订单表
 * </p>
 *
 * @author WuDi
 * @since 2023-07-17
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_rebate_order", autoResultMap = true)
public class RebateOrder extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单状态:1->正常状态;2->系统关闭;3->买家关闭订单;4->卖家关闭订单
     */
    private RebateOrderStatus status;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 买家id
     */
    private Long buyerId;

    /**
     * 买家昵称
     */
    private String buyerNickname;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 订单项id
     */
    private Long orderItemId;

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
    @TableField("product_type")
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
     * 成交价(活动价: 如秒杀,等等)
     */
    private Long dealPrice;

    /**
     * 返利百分比
     */
    private Long rebatePercentage;

    /**
     * 平台服务费百分比（抽佣、分润 百分比）
     */
    private Integer serviceFeePercent;

    /**
     * 平台服务费
     */
    private Long platformServiceFee;

    /**
     * 返利计算
     */
    private String rebateCalculation;

    /**
     * 预计返利
     */
    private Long estimatedRebate;

    /**
     * 结算状态
     */
    private SettlementStatus settlementStatus;

    /**
     * 已结算
     */
    @TableField(exist = false)
    private Long settled;

    /**
     * 待结算
     */
    @TableField(exist = false)
    private Long pendingSettlement;

    /**
     * 已失效
     */
    @TableField(exist = false)
    private Long expired;

    /**
     * 下单时间
     */
    @TableField(exist = false)
    private LocalDateTime orderTime;


    /**
     * 店铺返利合计
     */
    @TableField(exist = false)
    private Long shopTotalRebate;

    /**
     * 返利订单项
     */
    @TableField(exist = false)
    private List<RebateOrderItem> rebateOrderItems;


    @TableField(exist = false)
    private String ids;

    /**
     * 生成 sku key
     *
     * @return sku key
     */
    public ShopProductSkuKey skuKey() {
        return new ShopProductSkuKey().setShopId(getShopId()).setProductId(getProductId()).setSkuId(getSkuId());
    }

    public BigDecimal rebateRate() {
        return AmountCalculateHelper.getDiscountRate(rebatePercentage, CommonPool.UNIT_CONVERSION_TEN_THOUSAND);
    }
}
