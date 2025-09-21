package com.medusa.gruul.order.api.addon.freight;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 运费计算param
 *
 * @author xiaoq
 * @Description FreightParam
 * @date 2023-05-07 15:22
 */
@Data
public class FreightParam implements Serializable {

    @Serial
    private static final long serialVersionUID = -6519546919725394415L;
    /**
     * 物流运费模板表id
     */
    private Long templateId;


    /**
     * sku信息
     */
    private List<SkuInfoParam> skuInfos;
}

