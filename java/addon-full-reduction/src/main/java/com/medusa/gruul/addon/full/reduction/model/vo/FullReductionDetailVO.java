package com.medusa.gruul.addon.full.reduction.model.vo;

import com.medusa.gruul.addon.full.reduction.model.bo.FullReductionProduct;
import com.medusa.gruul.addon.full.reduction.model.bo.FullReductionRule;
import com.medusa.gruul.addon.full.reduction.model.enums.ProductType;
import com.medusa.gruul.global.model.o.RangeDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 张治保
 * @since 2024/6/17
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class FullReductionDetailVO {

    /**
     * 店铺 id
     */
    private Long shopId;

    /**
     * 满减活动id
     */
    private Long id;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动开始范围
     */
    private RangeDateTime time;

    /**
     * 活动规则
     */
    private List<FullReductionRule> rules;

    /**
     * 活动商品类型
     */
    private ProductType productType;

    /**
     * 活动商品
     */
    private List<FullReductionProduct> products;

}
