package com.medusa.gruul.goods.service.model.vo;

import com.medusa.gruul.storage.api.enums.LimitType;
import com.medusa.gruul.storage.api.enums.StockType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class CopySkuVO {

    /**
     * 规格值
     */
    private List<String> specs;

    /**
     * 初始库存 仅新增sku时 可以使用
     */
    private Long initStock;
    /**
     * 库存类型
     */
    private StockType stockType;

    /**
     * 初始销量
     */
    private Integer initSalesVolume;
    /**
     * 限购类型
     */
    private LimitType limitType;
    /**
     * 限购数量
     */
    private Integer limitNum;
    /**
     * sku图片
     */
    private String image;

    /**
     * 原价 单位豪
     */
    private BigDecimal price;

    /**
     * 真实销售价 单位豪 1豪 = 0.01分
     */
    private BigDecimal salePrice;

    /**
     * 规格重量 单位/千克
     */
    private BigDecimal weight;

    /**
     * 最低购买数量
     */
    private Integer minimumPurchase = 1;
}