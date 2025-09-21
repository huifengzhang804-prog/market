package com.medusa.gruul.addon.ic.modules.opens.uupt.model.order;

import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.OrderState;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@Setter
@ToString
public class OrderCallbackParam {

    /**
     * 否 UU跑腿订单编号
     */
    private String orderCode;

    /**
     * 否 第三方对接平台订单id
     */
    private String originId;

    /**
     * 是 当前状态1下单成功 3跑男抢单 4已到达 5已取件 6到达目的地 10收件人已收货 -1订单取消
     */
    private OrderState state;

    /**
     * 是 当前状态说明
     */
    private String stateText;

    /**
     * 是 跑男姓名(跑男接单后)
     */
    private String driverName;

    /**
     * 是 跑男电话(跑男接单后)
     */
    private String driverMobile;

    /**
     * 是 跑男头像(跑男接单后)
     */
    private String driverPhoto;

    /**
     * 是  状态变更时间戳（毫秒）
     */
    private Long changeTime;
}
