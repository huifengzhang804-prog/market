package com.medusa.gruul.order.service.model.bo;

import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.user.api.model.vo.MemberAggregationInfoVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 订单与会员信息
 *
 * @author 张治保
 * date 2023/2/6
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class MemberOrder implements Serializable {

    /**
     * 会员信息
     */
    private MemberAggregationInfoVO member;

    /**
     * 订单信息
     */
    private Order order;

    /**
     * 优惠券order
     */
    private Order couponOrder;


    /**
     * 是否是PC 客户端订单
     * todo 用于额外处理会员信息 后期会员调整回来 直接移除这个参数
     */
    private boolean pc;

}

