package com.medusa.gruul.addon.distribute.model.vo;

import com.medusa.gruul.addon.distribute.model.enums.DistributeOrderStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * date 2023/5/15
 */
@Getter
@Setter
@ToString
public class DistributorOrderVO implements Serializable {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 店铺id
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
     * skuId
     */
    private Long skuId;

    /**
     * sku图片
     */
    private String image;

    /**
     * 是否属于内购订单
     */
    private Boolean purchase;

    /**
     * 规格
     */
    private List<String> specs;

    /**
     * 购买数量
     */
    private Integer num;

    /**
     * 最终成交价
     */
    private Long dealPrice;

    /**
     * 订单状态
     */
    private DistributeOrderStatus orderStatus;

    /**
     * 一级分销商与佣金
     */
    private DistributorBonusVO one;

    /**
     * 二级分销商与佣金
     */
    private DistributorBonusVO two;

    /**
     * 三级分销商与佣金
     */
    private DistributorBonusVO three;

    /**
     * 分佣配置
     */
    private BonusShareVO bonusShare;


}
