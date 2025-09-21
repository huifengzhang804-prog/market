package com.medusa.gruul.addon.ic.modules.opens.uupt.api;

import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptResponse;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.PushType;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.order.*;

/**
 * @author 张治保
 * @since 2024/8/10
 */
public interface IOrderApi extends IBaseUuptApi {

    @Override
    default String apiPrefix() {
        return "/order/";
    }

    /**
     * 获取开放城市列表
     * <a href="https://open.uupt.com/#/ApiDocument/v3/InterfaceList/openCityList?PlatType=PLAT_MERCHANT&version=3&t=%E8%8E%B7%E5%8F%96%E5%BC%80%E6%94%BE%E5%9F%8E%E5%B8%82%E5%88%97%E8%A1%A8&index=1-5-1">
     * 获取UU已开通的城市
     * </a>
     *
     * @return 开放城市列表
     */
    default UuptResponse<OpenCityListResp> openCityList() {
        return withOpenId(
                config().getOpenId(),
                () -> request(
                        null, new TypeReference<OpenCityListResp>() {
                        }
                )
        );
    }

    /**
     * 获取订单价格
     * <a href="https://open.uupt.com/#/ApiDocument/v3/InterfaceList/orderPrice?PlatType=PLAT_MERCHANT&version=3&t=%E8%8E%B7%E5%8F%96%E8%AE%A2%E5%8D%95%E4%BB%B7%E6%A0%BC&index=1-5-2">
     * 获取订单价格
     * </a>
     *
     * @return 获取订单价格
     */
    default UuptResponse<OrderPriceResp> orderPrice(String openId, OrderPriceParam param) {
        return withOpenId(
                openId,
                () -> request(
                        param,
                        new TypeReference<OrderPriceResp>() {
                        }
                )
        );
    }

    /**
     * 发布订单
     * <a href="https://open.uupt.com/#/ApiDocument/v3/InterfaceList/addOrder?PlatType=PLAT_MERCHANT&version=3&t=%E5%8F%91%E5%B8%83%E8%AE%A2%E5%8D%95&index=1-5-3">
     * 发布订单
     * </a>
     *
     * @return 发布订单结果
     */
    default UuptResponse<OrderKey> addOrder(String openId, AddOrderParam param) {
        param.setPushType(config().isTest() ? PushType.TEST_ORDER : PushType.OPEN_ORDER);
        return withOpenId(
                openId,
                () -> request(
                        param,
                        new TypeReference<OrderKey>() {
                        }
                )
        );
    }


    /**
     * 发布订单
     * <a href="https://open.uupt.com/#/ApiDocument/v3/InterfaceList/addOrder?PlatType=PLAT_MERCHANT&version=3&t=%E5%8F%91%E5%B8%83%E8%AE%A2%E5%8D%95&index=1-5-3">
     * 发布订单
     * </a>
     *
     * @return 发布订单结果
     */
    default UuptResponse<OrderDetailResp> orderDetail(String openId, OrderKey param) {
        return withOpenId(
                openId,
                () -> request(
                        param,
                        new TypeReference<OrderDetailResp>() {
                        }
                )
        );
    }

    /**
     * 获取取消费用
     * <a href="https://open.uupt.com/#/ApiDocument/v3/InterfaceList/cancelFee?PlatType=PLAT_MERCHANT&version=3&t=%E8%8E%B7%E5%8F%96%E5%8F%96%E6%B6%88%E8%B4%B9&index=1-5-5">
     * 获取取消费用
     * </a>
     *
     * @return 取消费用明细
     */
    default UuptResponse<CancelFeeResp> cancelFee(String openId, OrderKey param) {
        return withOpenId(
                openId,
                () -> request(
                        param,
                        new TypeReference<CancelFeeResp>() {
                        }
                )
        );
    }

    /**
     * 取消订单
     * <a href="https://open.uupt.com/#/ApiDocument/v3/InterfaceList/cancelOrder?PlatType=PLAT_MERCHANT&version=3&t=%E5%8F%96%E6%B6%88%E8%AE%A2%E5%8D%95&index=1-5-6">
     * 取消订单
     * </a>
     *
     * @return 取消订单结果
     */
    default UuptResponse<CancelOrderResp> cancelOrder(String openId, CancelOrderParam param) {
        return withOpenId(
                openId,
                () -> request(
                        param,
                        new TypeReference<CancelOrderResp>() {
                        }
                )
        );
    }

    /**
     * 加小费
     * <a href="https://open.uupt.com/#/ApiDocument/v3/InterfaceList/addGratuity?PlatType=PLAT_MERCHANT&version=3&t=%E5%8A%A0%E5%B0%8F%E8%B4%B9&index=1-5-7">
     * 加小费，发单成功后、接单前可进行增加小费
     * </a>
     *
     * @return 取消订单结果
     */
    default UuptResponse<Void> addGratuity(String openId, AddGratuityParam param) {
        return withOpenId(
                openId,
                () -> request(
                        param,
                        new TypeReference<Void>() {
                        }
                )
        );
    }


    /**
     * 同步取件码给骑手
     * <a href="https://open.uupt.com/#/ApiDocument/v3/InterfaceList/syncPickupCode?PlatType=PLAT_MERCHANT&version=3&t=%E5%90%8C%E6%AD%A5%E5%8F%96%E4%BB%B6%E7%A0%81%E7%BB%99%E9%AA%91%E6%89%8B&index=1-5-8">
     * 用于骑手取件时需要给店家提供取件码的场景，接单后、取件前允许调用该接口
     * </a>
     *
     * @return 取消订单结果
     */
    default UuptResponse<Void> syncPickupCode(String openId, SyncPickupCodeParam param) {
        return withOpenId(
                openId,
                () -> request(
                        param,
                        new TypeReference<Void>() {
                        }
                )
        );
    }

}
