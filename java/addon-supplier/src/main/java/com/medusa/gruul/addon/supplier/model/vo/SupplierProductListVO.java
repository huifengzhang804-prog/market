package com.medusa.gruul.addon.supplier.model.vo;

import com.medusa.gruul.goods.api.model.dto.ProductExtraDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.storage.api.entity.StorageSku;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 供应商商品列表VO
 *
 * @author xiaoq
 * @Description SupplierProductListVO.java
 * @date 2023-07-17 16:56
 */
@Getter
@Setter
@ToString
public class SupplierProductListVO {

    private Long id;

    private Long shopId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 联系方式
     */
    private String supplierContractNumber;

    /**
     * 供应商商品名称
     */
    private String productName;

    /**
     * 销售类型
     */
    private SellType sellType;

    /**
     * 供应商商品状态
     */
    private ProductStatus status;

    /**
     * 商品类型
     */
    private ProductType productType;

    /**
     * 额外数据
     */
    private ProductExtraDTO extra;

    /**
     * 销售价格[1000,1100,1200]
     */
    private List<Long> salePrices;

    /**
     * 最大价格
     */
    private Long maxPrice;
    /**
     * 最小价格
     */
    private Long minPrice;
    /**
     * 销售价格
     */
    private Long salePrice;

    /**
     * 画册
     */
    private String albumPics;

    /**
     * 商品仓储
     */
    private List<StorageSkuVO> storageSkus;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 所属名称
     */
    private String shopName;


    /**
     * 运费模板id
     */
    private Long freightTemplateId;


    private Boolean deleted;

    /**
     * 采集地址
     */
    private String collectionUrl;
}
