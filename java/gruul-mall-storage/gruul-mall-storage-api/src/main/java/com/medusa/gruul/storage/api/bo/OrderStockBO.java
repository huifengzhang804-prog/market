package com.medusa.gruul.storage.api.bo;

import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * sku 库存 消费 dto
 *
 * @author 张治保
 * date 2022/9/22
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class OrderStockBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -827921959875999619L;
    /**
     * 单号
     */
    @NotBlank
    private String no;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * sku key与库存销量映射 map
     */
    @NotNull
    @Size(min = 1)
    private Map<ActivityShopProductSkuKey, StSvBo> skuKeyStSvMap;

}
