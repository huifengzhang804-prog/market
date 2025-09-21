package com.medusa.gruul.order.api.helper;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;

/**
 * 订单通用工具类
 * 订单号统一算法
 *
 * @author 张治保
 * @since 2024/6/17
 */
public interface OrderHelper {

    String ORDER_NO_PREFIX = "SS";

    /**
     * 生成订单号
     *
     * @return 订单号
     */
    static String orderNo() {
        return orderNo(ORDER_NO_PREFIX, IdUtil.getSnowflakeNextId());
    }

    /**
     * 生成订单号
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 订单号
     */
    static String orderNo(String prefix, long suffix) {
        return prefix + suffix;
    }

    /**
     * 获取订单号中的数字部分
     *
     * @param orderNo 订单号
     * @return 订单号中的数字部分
     */
    static long getOrderNoNumber(String orderNo) {
        return Long.parseLong(orderNo.substring(ORDER_NO_PREFIX.length()));
    }


    /**
     * 店铺订单号 转 主订单号
     *
     * @param shopOrderNo 店铺订单号
     * @return 主订单号
     */
    static String shopOrderNoToOrderNo(String shopOrderNo) {
        if (StrUtil.isEmpty(shopOrderNo)) {
            return null;
        }
        return shopOrderNo.substring(0, shopOrderNo.lastIndexOf(StrPool.DASHED));
    }

    /**
     * 获取店铺订单号 主订单号  + 店铺订单顺序
     *
     * @param orderNo 主订单号
     * @param order   店铺订单顺序
     * @return 店铺订单号
     */
    static String shopOrderNo(String orderNo, int order) {
        return orderNo + StrPool.DASHED + order;
    }


    /**
     * 商品属性转规格
     *
     * @param attrName  属性名
     * @param attrPrice 属性价格
     * @return 规格
     */
    static String attr2Spec(String attrName, Long attrPrice) {
        return StrUtil.emptyIfNull(attrName).concat(StrUtil.COLON)
                .concat(AmountCalculateHelper.toYuan(attrPrice).toPlainString());
    }


}
