package com.medusa.gruul.storage.api.dto;

import cn.hutool.json.JSONObject;
import com.medusa.gruul.common.model.enums.SellType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * date 2022/7/12
 */
@Getter
@Setter
@ToString
public class StorageSpecSkuDTO implements Serializable {
    /**
     * 商品id
     */
    @NotNull
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;


    /**
     * 销售类型
     */
    private SellType sellType;


    /**
     * 商品当前状态
     */
    private JSONObject productCurrentStatus;

    /**
     * 规格组
     */
    @Valid
    private List<SpecGroupDTO> specGroups;

    /**
     * sku列表
     */
    @NotNull
    @Size(min = 1)
    @Valid
    private List<SkuDTO> skus;

}
