package com.medusa.gruul.storage.api.vo;

import com.medusa.gruul.storage.api.entity.StorageSku;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保 date 2022/7/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductSpecsSkusVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 规格组
     */
    private List<ProductSpecVO> specGroups;
    /**
     * sku列表
     */
    private List<StorageSku> skus;
}
