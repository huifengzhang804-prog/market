package com.medusa.gruul.addon.supplier.model.bo;

import com.medusa.gruul.addon.supplier.model.enums.PackageStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/7/29
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public class PublishProductKey implements Serializable {

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
     * 订单号
     */
    private String orderNo;
    /**
     * 订单项 id
     */
    private Long itemId;
    /**
     * 数量
     */
    private Integer count;
    /**
     *
     */
    private PackageStatus packageStatus;


}
