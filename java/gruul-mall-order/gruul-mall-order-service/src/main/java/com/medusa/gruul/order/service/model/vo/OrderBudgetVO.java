package com.medusa.gruul.order.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 张治保
 * @since 2024/2/3
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderBudgetVO implements Serializable {

    /**
     * 预创建的订单信息 （暂时置空）需要再填充数据
     */
    private OrderRespVO orderInfo;

    /**
     * 商品总价
     */
    private Long total;

    /**
     * 店铺优惠
     */
    private Long shopDiscount;

    /**
     * 平台优惠
     */
    private Long platformDiscount;

    /**
     * 会员优惠
     */
    private Long memberDiscount;

    /**
     * 总运费
     */
    private Long freight;

    /**
     * 实付金额
     */
    private Long payAmount;


    /**
     * 店铺满减 优惠的金额 key:店铺 id，value 满减优惠金额
     */
    private Map<Long, Long> shopFull;

    /**
     * 店铺运费 key:店铺 id，value 运费
     */
    private Map<Long, Long> shopFreight;


    /**
     * 增加会员折扣
     *
     * @param value 增加的值
     */
    public void incrMemberDiscount(long value) {
        memberDiscount += value;
    }

    /**
     * 增加平台折扣
     *
     * @param value 增加的值
     */
    public void incrPlatformDiscount(long value) {
        platformDiscount += value;
    }

    /**
     * 增加店铺折扣
     *
     * @param value 增加的值
     */
    public void incrShopDiscount(long value) {
        shopDiscount += value;
    }


}
