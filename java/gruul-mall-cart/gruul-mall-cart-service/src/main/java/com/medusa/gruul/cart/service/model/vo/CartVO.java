package com.medusa.gruul.cart.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车详情
 *
 * @author 张治保
 * date 2022/5/16
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CartVO implements Serializable {

    /**
     * 正常的数据列表
     */
    private List<ShopProductSkuVO> valid;
    /**
     * 失效的
     */
    private List<ShopProductSkuVO> invalid;

    public CartVO(){
        this.valid = new ArrayList<>();
        this.invalid = new ArrayList<>();
    }
}
