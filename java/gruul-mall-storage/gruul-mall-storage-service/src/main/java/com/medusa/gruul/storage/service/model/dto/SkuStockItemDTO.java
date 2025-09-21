package com.medusa.gruul.storage.service.model.dto;

import com.medusa.gruul.storage.api.enums.StockType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 库存消费记录 dto
 *
 * @author 张治保
 * date 2022/9/22
 */
@Data
public class SkuStockItemDTO implements Serializable {


    /**
     * 商品id
     */
    private Long productId;

    /**
     * sku id
     */
    @NotNull
    private Long skuId;

    /**
     * 变化数量
     */
    @NotNull
    private Long num;

    /**
     * 库存类型
     */
    @NotNull
    private StockType stockType;


    /**
     * 实时库存
     */
    private Long stock;

    /**
     * 产品规格
     */
    private List<String> specs;


}
