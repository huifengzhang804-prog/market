package com.medusa.gruul.storage.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * @author 张治保
 * date 2023/7/27
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class StoragesCopyDTO implements Serializable {

    /**
     * 业务流水号
     */
    @NotBlank
    private String transactionId;

    /**
     * 目标店铺 id
     */
    private Long targetShopId;

    /**
     * 库存sku列表 修改/新增
     */
    @NotNull
    @Size(min = 1)
    private Set<SkuCopy> targets;

}
