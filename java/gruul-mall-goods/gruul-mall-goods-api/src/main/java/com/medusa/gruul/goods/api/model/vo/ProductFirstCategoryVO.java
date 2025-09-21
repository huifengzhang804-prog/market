package com.medusa.gruul.goods.api.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>一级分类对应的商品数据VO</p>
 *
 * @author Andy.Yan
 */
@Data
@Accessors(chain = true)
public class ProductFirstCategoryVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2324444842752859472L;

    /**
     * 类目ID
     */
    private String id;

    /**
     * 类目名称
     */
    private String name;

    /**
     * 类目对应的商品数量
     */
    private Long productNum;
}
