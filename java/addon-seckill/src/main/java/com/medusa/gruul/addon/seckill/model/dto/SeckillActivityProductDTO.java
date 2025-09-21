package com.medusa.gruul.addon.seckill.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/5/28
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class SeckillActivityProductDTO implements Serializable {

    /**
     * 商品 id
     */
    @NotNull
    private Long id;

    /**
     * 该商品 sku列表
     */
    @NotEmpty
    private Set<@Valid SeckillActivitySkuDTO> skus;


}
