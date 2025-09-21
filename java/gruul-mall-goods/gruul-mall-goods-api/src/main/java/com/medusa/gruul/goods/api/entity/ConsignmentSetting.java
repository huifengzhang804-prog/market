package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.goods.api.model.enums.PricingType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author miskw
 * @date 2023/8/8
 * @describe 代销设置
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_consignment_setting", autoResultMap = true)
public class ConsignmentSetting extends BaseEntity {

    /**
     * 店铺id
     */
    @TableField("`shop_id`")
    private Long shopId;

    /**
     * 设价方式
     */
    @TableField("`type`")
    private PricingType type;

    /**
     * 销售价比值
     */
    @TableField("`sale`")
    private Long sale;

    /**
     * 划线价比值
     */
    @TableField("`scribe`")
    private Long scribe;

}
