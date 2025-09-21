package com.medusa.gruul.addon.supplier.model.dto;

import com.medusa.gruul.goods.api.model.dto.ProductViolationDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * 商品状态DTO
 *
 * @author xiaoq
 * @Description ProductStatusDTO.java
 * @date 2023-06-19 14:40
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductStatusChangeDTO {

    /**
     * 商品id
     */
    @NotNull
    @Size(min = 1)
    private Set<Long> productIds;

    /**
     * 违规信息
     */
    private ProductViolationDTO productViolation;


    /**
     * 拒绝说明
     */
    private String explain;

}
