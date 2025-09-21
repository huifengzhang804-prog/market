package com.medusa.gruul.addon.platform.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 平台店铺签约类目
 *
 * @author xiaoq
 * @Description PlatformShopSigningCategory.class
 * @date 2023-05-12 16:35
 */

@Getter
@Setter
@Accessors(chain = true)
@TableName("t_platform_shop_signing_category")
@ToString
public class PlatformShopSigningCategory extends BaseEntity {
    /**
     * 上级类目id  平台一级类目
     */
    private Long parentId;


    /**
     * 当前类目id 平台二级类目
     */
    private Long currentCategoryId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 自定义扣除比例
     */
    private Long customDeductionRatio;



}
