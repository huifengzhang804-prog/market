package com.medusa.gruul.addon.matching.treasure.model.vo;

import com.medusa.gruul.addon.matching.treasure.model.enums.SetMealStatus;
import com.medusa.gruul.addon.matching.treasure.model.enums.SetMealType;
import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class SetMealVO {


    /**
     * 套餐id
     */
    private Long id;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 套餐描述
     */
    private String setMealDescription;

    /**
     * 套餐名称
     */
    private String setMealName;

    /**
     * 套餐主图
     */
    private String setMealMainPicture;

    /**
     * 套餐类型 [0:自选商品套餐 1:固定组合套餐]
     */
    private SetMealType setMealType;
    /**
     * 套餐类型描述
     */
    private String setMealTypeDesc;

    /**
     * 活动状态 [0:未开始 1:进行中 2:已结束 3:违规下架]
     */
    private SetMealStatus setMealStatus;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 是否可叠加优惠（会员，优惠券，满减）
     */
    private StackableDiscount stackable;

    /**
     * 参与人数
     */
    private Integer peopleNum;

    /**
     * 应收金额
     */
    private Long amountReceivable;


    /**
     * 套餐配送类型
     */
    private DistributionMode distributionMode;

    /**
     * 店铺运营模式
     */
    private ShopMode shopMode;

    /**
     * 主商品
     */
    private List<SetMealProductVO> mainProduct;

    /**
     * 搭配商品
     */
    private List<SetMealProductVO> matchingProducts;

    /**
     * 违规说明
     */
    private String violationExplain;
    /**
     * 商品数量
     */
    private Integer productCount;
    /**
     * 完成订单数量
     */
    private Integer orderCount;


}
