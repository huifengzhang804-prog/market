package com.medusa.gruul.addon.supplier.model.vo;

import com.medusa.gruul.goods.api.model.enums.ProductType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 待发布商品信息
 *
 * @author 张治保
 * date 2023/7/29
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PublishProductVO implements Serializable {

    /**
     * 店铺 id
     */
    private Long shopId;

    /**
     * 供应商 id
     */
    private Long supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 联系方式
     */
    private String supplierContractNumber;

    /**
     * 商品 id
     */
    private Long productId;

    /**
     * 商品类型
     */
    private ProductType productType;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 商品价格
     */
    private List<Long> prices;

    /**
     * 采购数量
     */
    private Long num;
    /**
     * 已入库数量
     */
    private Long stockInCount;
    /**
     * 待发货数量
     */
    private Long waitForDeliveryCount;
    /**
     * 已发货数量
     */
    private Long hasDeliveryCount;
}
