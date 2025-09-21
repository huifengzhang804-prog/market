package com.medusa.gruul.addon.supplier.model.vo;

import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.enums.LimitType;
import com.medusa.gruul.storage.api.enums.StockType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author xiaoq
 * @Description
 * @date 2023-08-03 11:09
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class StorageSkuVO implements Serializable {

    private Long id;
    /**
     * 库存类型
     */
    private OrderType activityType;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 库存类型 1 无限库存 2 有限
     */
    private StockType stockType;

    /**
     * 商品库存
     */
    private Long stock;

    /**
     * 销量
     */
    private Long salesVolume;

    /**
     * 初始销量
     */
    private Long initSalesVolume;

    /**
     * 限购类型 1 不限购 , 2 商品限购 , 3 规格限购
     */
    private LimitType limitType;

    /**
     * 限购数量
     */
    private Integer limitNum;

    /**
     * 规格名称列表
     */
    private List<String> specs;

    /**
     * sku图片
     */
    private String image;

    /**
     * 原价 单位 豪 1豪 = 0.01分
     */
    private Long price;

    /**
     * 真实销售价 单位豪 1豪 = 0.01分
     */
    private Long salePrice;


    /**
     * 重量
     */
    private BigDecimal weight;


    /**
     * 最低购买量
     */
    private Integer minimumPurchase;


    /**
     * 店铺自有商品SKU库存数
     */
    private Long shopOwnProductStockNum;


    public static StorageSkuVO fromStorageSku(StorageSku sku) {
        return new StorageSkuVO()
                .setId(sku.getId())
                .setImage(sku.getImage())
                .setActivityType(sku.getActivityType())
                .setPrice(sku.getPrice())
                .setShopId(sku.getShopId())
                .setProductId(sku.getProductId())
                .setLimitNum(sku.getLimitNum())
                .setInitSalesVolume(sku.getInitSalesVolume())
                .setLimitType(sku.getLimitType())
                .setMinimumPurchase(sku.getMinimumPurchase())
                .setSpecs(sku.getSpecs())
                .setStock(sku.getStock())
                .setSalePrice(sku.getSalePrice())
                .setSalesVolume(sku.getSalesVolume())
                .setStockType(sku.getStockType())
                .setWeight(sku.getWeight())
                ;
    }
}
