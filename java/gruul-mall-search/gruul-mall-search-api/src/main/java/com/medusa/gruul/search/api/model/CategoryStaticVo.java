package com.medusa.gruul.search.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 统计信息
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CategoryStaticVo implements Serializable {
    /**
     * 商品数量
     */
    private Long productCount=0L;
    /**
     * 商品销量
     */
    private Long salesCount=0L;

    public void addProductCount(Long productCount){
        this.productCount+=productCount;
    }
    public void addSalesCount(Long salesCount){
        this.salesCount+=salesCount;
    }
}
