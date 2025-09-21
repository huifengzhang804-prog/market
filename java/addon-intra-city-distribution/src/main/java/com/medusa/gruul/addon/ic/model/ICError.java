package com.medusa.gruul.addon.ic.model;

import com.medusa.gruul.global.model.exception.Error;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/13
 */
@RequiredArgsConstructor
public enum ICError implements Error {

    /**
     * 不支持同城配送配送方式
     */
    IC_UNACTIVATED(160001, "商户未开启同城配送服务"),

    /**
     * 超出配送范围
     */
    OUT_OF_RANGE(160002, "超出配送范围"),

    /**
     * 不满足起送金额
     */
    BELOW_MIN_LIMIT(160003, "订单金额不满足商家配送起步金额"),

    /**
     * uupt 开放平台配置不正确
     */
    WRONG_OPEN_API_CONFIG(160004, "开放平台配置不正确"),

    /**
     * 店铺配送单已被其他人接单
     */
    ORDER_TAKEN(160005, "该单已被其他人接单"),

    /**
     * 订单已不能取消
     */
    ORDER_CANNOT_OFFER(160006, "当前订单已不可取消"),

    /**
     * 订单状态已不可处理
     */
    ORDER_STATUS_CANNOT_PROCESS(160007, "订单处理已结束，无法继续处理"),

    /**
     * uu 跑腿未开通
     */
    UUPT_UNACTIVATED(160008, "未开通uu跑腿服务"),

    /**
     * uu跑腿未开通当前城市服务
     */
    UUPT_CITY_UNACTIVATED(160009, "uu跑腿未开通当前城市服务"),

    /**
     * 未查询到用户收货地址
     */
    USER_ADDRESS_ISNULL(160010, " 未查询到用户收货地址"),

    /**
     * uupt 响应发生异常
     */
    UUPT_RESP_ERROR(160011, "UU跑腿响应错误"),

    /**
     * 请先进行配送价格计算 否则不能发布 UU 跑腿订单
     */
    UUPT_ORDER_PRICE_FIRST(160012, "请先进行配送价格计算"),

    /**
     * 订单流转中，不能重新处理
     */
    IC_ORDER_PROCESSING(160013, "配送订单流转中，不能重新处理"),

    /**
     * 订单已结束，无法继续处理
     */
    IC_ORDER_FINISHED(160014, "订单已结束，无法继续处理"),

    /**
     * 配送单暂未被抢单
     */
    IC_ORDER_NOT_PICKUP(160015, "配送单暂未被抢单"),

    /**
     * 配送单异常，无法处理
     */
    IC_ORDER_ERROR(160016, "配送单异常，无法处理"),

    /**
     * uupt 余额不足
     */
    UUPT_BALANCE_NOT_ENOUGH(160017, "UU跑腿账户余额不足，请充值！！！"),
    ;

    private final int code;

    private final String msg;

    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return msg;
    }
}
