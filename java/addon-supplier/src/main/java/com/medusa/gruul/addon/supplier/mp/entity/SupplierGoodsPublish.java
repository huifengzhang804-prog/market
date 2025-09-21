package com.medusa.gruul.addon.supplier.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2023/7/29
 */
@Getter
@Setter
@ToString
@TableName("t_supplier_goods_publish")
@Accessors(chain = true)
public class SupplierGoodsPublish implements Serializable {

    /**
     * 店铺 id
     */
    private Long shopId;

    /**
     * 供应商 id
     */
    private Long supplierId;

    /**
     * 商品 id
     */
    private Long productId;

    /**
     * 是否已发布
     */
    private Boolean published;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
