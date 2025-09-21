package com.medusa.gruul.addon.distribute.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.distribute.model.enums.DistributionStatus;
import com.medusa.gruul.addon.distribute.model.enums.ShareType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import java.io.Serial;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 分销商品关联
 *
 * @author 张治保
 * @since 2022-11-14
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_distribute_product", autoResultMap = true)
public class DistributeProduct extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 销量
     */
    private Long sales;

    /**
     * 商品名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 商品主图
     */
    private String pic;

    /**
     * 商品价格
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<Long> salePrices;

    /**
     * 商品状态
     */
    private ProductStatus status;

    /**
     * 分销状态 0-->分销中 1-->取消
     */
    private DistributionStatus distributionStatus;

    /**
     * 佣金类型 佣金类型 1.统一设置 2.固定金额 3.百分比
     */
    private ShareType shareType;

    /**
     * 一级分佣
     */
    private Long one;

    /**
     * 二级分佣
     */
    private Long two;

    /**
     * 三级分佣
     */
    private Long three;

    /**
     * 商品类型
     */
    @TableField("product_type")
    private ProductType productType;

    /**
     * 店铺名称
     */
    @TableField(exist = false)
    private String shopName;

    /**
     * 总库存
     */
    @TableField(exist = false)
    private Long stock;


    /**
     * 预计获得的佣金
     */
    @TableField(exist = false)
    private Long bonus;

    /**
     * 最高价
     */
    @TableField(exist = false)
    private Long maxPrice;
    /**
     * 是否可以编辑
     */
    @TableField(exist = false)
    private Boolean canEdit = Boolean.FALSE;
}
