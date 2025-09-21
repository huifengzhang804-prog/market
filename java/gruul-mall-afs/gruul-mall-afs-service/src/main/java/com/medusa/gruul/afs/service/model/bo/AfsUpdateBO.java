package com.medusa.gruul.afs.service.model.bo;

import com.medusa.gruul.afs.service.mp.entity.AfsOrder;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/22
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AfsUpdateBO {

    /**
     * 售后工单号
     */
    private String afsNo;

    /**
     * 售后工单
     */
    private AfsOrder afsOrder;

    /**
     * 订单商品项
     */
    private ShopOrderItem shopOrderItem;

    /**
     * 下一个售后状态
     */
    private AfsStatus nextStatus;

    /**
     * 当前登陆用户
     */
    private Boolean isSystem;

    /**
     * 售后成功退款的交易流水号
     */
    private String afsTradeNo;

}
