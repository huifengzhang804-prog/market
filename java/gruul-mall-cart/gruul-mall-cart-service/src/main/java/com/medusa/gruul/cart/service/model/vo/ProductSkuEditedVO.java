package com.medusa.gruul.cart.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 携带编辑后的信息
 * @author 张治保
 * date 2022/5/16
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductSkuEditedVO extends ProductSkuVO {
    /**
     * 修改后的sku图片
     */
    private String editedImage;
    /**
     * 修改后的该商品Sku的单价
     */
    private Long editedPrice;
    /**
     * 修改后的规格
     */
    private List<String> editedSpecs;


}
