package com.medusa.gruul.storage.api.dto;

import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.storage.api.enums.LimitType;
import com.medusa.gruul.storage.api.enums.StockType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * sku信息
 *
 * @author 张治保
 */
@Getter
@Setter
@ToString
public class SkuDTO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 规格名称
     */
    private List<String> specs;
    /**
     * 库存类型
     */
    @NotNull
    private StockType stockType;

    /**
     * 初始库存 仅新增sku时 可以使用
     */
    private Long initStock;

    /**
     * 初始销量
     */
    @NotNull
    @Min(0)
    private Long initSalesVolume;
    /**
     * 限购类型
     */
    @NotNull
    private LimitType limitType;
    /**
     * 限购数量
     */
    @NotNull
    @Min(0)
    private Integer limitNum;
    /**
     * sku图片
     */
    private String image;

    /**
     * 原价 单位豪
     */
    @NotNull
    @Min(0)
    private Long price;

    /**
     * 真实销售价 单位豪 1豪 = 0.01分
     */
    @NotNull
    @Min(0)
    private Long salePrice;

    /**
     * 规格重量 单位/千克
     */
    private BigDecimal weight;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 最低购买量
     */
    private Integer minimumPurchase = CommonPool.NUMBER_ONE;
}