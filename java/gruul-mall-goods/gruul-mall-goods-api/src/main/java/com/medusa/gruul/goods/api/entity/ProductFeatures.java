package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesValueDTO;
import com.medusa.gruul.goods.api.model.enums.FeaturesType;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 产品特性表
 *
 * @author xiaoq
 * @Description ProductFeatures.java
 * @date 2023-06-14 19:52
 */
@Data
@Valid
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_product_features", autoResultMap = true)
public class ProductFeatures extends BaseEntity {
    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 特性type
     */
    private FeaturesType featuresType;

    /**
     * 排序值
     */
    private Integer sort;


    /**
     * 业务处理字段
     */

    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private ProductFeaturesValueDTO featuresValue;

}
