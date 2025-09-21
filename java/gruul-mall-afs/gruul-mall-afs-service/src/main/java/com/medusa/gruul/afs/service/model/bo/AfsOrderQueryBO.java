package com.medusa.gruul.afs.service.model.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/11
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class AfsOrderQueryBO {

    /**
     * 是否查询单独查询售后历史
     */
    private Boolean history;
    /**
     * 售后工单号
     */
    private String afsNo;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 买家id
     */
    private Long buyerId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品订单id
     */
    private Long itemId;

    /**
     * 供应商id
     */
    private Long supplierId;

}
