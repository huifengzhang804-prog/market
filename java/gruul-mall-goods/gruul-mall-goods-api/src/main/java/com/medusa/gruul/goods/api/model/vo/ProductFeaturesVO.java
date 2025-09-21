package com.medusa.gruul.goods.api.model.vo;

import com.medusa.gruul.goods.api.model.dto.ProductFeaturesValueDTO;
import com.medusa.gruul.goods.api.model.enums.FeaturesType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商品特性vo
 *
 * @author xiaoq
 * @Description ProductFeaturesVO.java
 * @date 2023-06-16 15:00
 */
@Data
@Accessors(chain = true)
public class ProductFeaturesVO {

    private Long id;
    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 特性type
     */
    private FeaturesType featuresType;


    /**
     * 业务处理字段
     */
    private ProductFeaturesValueDTO featuresValue;
}
