package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 供应商评分记录
 *
 * @author xiaoq
 * @ description SupplierRateRecord.java
 * @date 2022-12-07 13:16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_supplier_rate_record")
public class SupplierRateRecord extends BaseEntity {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 供应商id
     */
    private Long supplierId;

    /**
     * 评分
     */
    private Integer rate;

    /**
     * 评价数量
     */
    @TableField(exist = false)
    private Integer evaluateNum;
}


