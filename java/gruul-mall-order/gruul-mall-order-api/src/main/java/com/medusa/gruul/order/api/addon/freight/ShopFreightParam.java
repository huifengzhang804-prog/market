package com.medusa.gruul.order.api.addon.freight;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 店铺运费计算param
 *
 * @author xiaoq
 * @Description 店铺运费计算param
 * @date 2023-05-07 11:40
 */
@Data
public class ShopFreightParam implements Serializable {

    private static final long serialVersionUID = 7855139823933298132L;
    /**
     * 店铺id
     */
    private  Long shopId;

    /**
     * 模板dto
     */
    List<FreightParam> freights;

}
