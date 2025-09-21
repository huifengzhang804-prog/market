package com.medusa.gruul.goods.service.model.vo;

import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.goods.api.model.dto.ProductExtraDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import com.medusa.gruul.storage.api.entity.StorageSku;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购发布商品VO
 *
 * @author xiaoq
 * @Description SupplierIssueProductListVO.java
 * @date 2023-07-29 14:36
 */
@Data
public class SupplierIssueProductListVO {

    private Long id;


    /**
     * 供应商id(S2B2C)
     */
    private Long supplierId;


    private SellType sellType;

    /**
     * 供应商名称(S2B2C)
     */
    private String supplierName;
    /**
     * 供应商联系电话
     */
    private String supplierContractNumber;


    /**
     * 商品名称
     */
    private String name;

    /**
     * 展示图片
     */
    private String pic;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 状态(默认上架，0--下架，1--上架)
     */
    private ProductStatus status;


    /**
     * 销售价格
     */
    private List<Long> salePrices;


    /**
     * 附加数据
     */
    private ProductExtraDTO extra;


    /**
     * 商品仓储
     */
    private List<StorageSku> storageSkus;


    private Boolean deleted;

    private ProductType productType;

    private LocalDateTime createTime;
}
