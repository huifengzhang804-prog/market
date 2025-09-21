package com.medusa.gruul.goods.api.model.vo;

import com.medusa.gruul.goods.api.model.dto.ProductExtraDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import com.medusa.gruul.storage.api.entity.StorageSku;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 商品库存基础信息VO
 *
 * @author xiaoq
 * @Description ProductStockVO.java
 * @date 2023-07-19 15:12
 */
@Data
@Accessors(chain = true)
public class ProductStockVO {
    /**
     * 商品id
     */
    private Long id;

    /**
     * 展示图片
     */
    private String pic;


    private String productName;

    private ProductType productType;

    private ProductStatus status;

    private Long shopId;

    private String albumPics;

    private Boolean isDeleted;

    /**
     * 附加数据
     */
    private ProductExtraDTO extra;


    /**
     * 商品仓储
     */
    private List<StorageSku> storageSkus;

}
