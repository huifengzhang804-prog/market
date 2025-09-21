package com.medusa.gruul.goods.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author  xiaoq
 * 商品数量
 */
@Getter
@Setter
@ToString
public class ProductNumVO {
    private Long platformCategoryId;
    private Integer num;
}
