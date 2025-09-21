package com.medusa.gruul.goods.api.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author miskw
 * @date 2023/8/7
 * @describe 描述
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SupplierGoodsUpdateStatusDTO implements Serializable {
    /**
     *  更新商品状态数据
     */
    private List<ProductUpdateStatusDTO> productUpdateStatus;
    /**
     * 违禁原因
     */
    private ProductViolationDTO productViolation;

}
