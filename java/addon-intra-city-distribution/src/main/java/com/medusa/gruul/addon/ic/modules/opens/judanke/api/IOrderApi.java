package com.medusa.gruul.addon.ic.modules.opens.judanke.api;

import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.addon.ic.modules.opens.judanke.JudankeResponse;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.order.*;

/**
 * @author 张治保
 * @since 2024/8/6
 */
public interface IOrderApi extends IBaseJudankeApi {

    /**
     * <a href="https://open.judanke.cn/docs/130/86">预创建订单</a>
     * 创建订单后，订单状态为新订单，后续调用以下接口可进行发单：
     * <a href="https://open.judanke.cn/docs/130/61">获取报价（根据订单ID）</a>
     * <a href="https://open.judanke.cn/docs/130/70">发起配送（根据订单ID)</a>
     *
     * @param param 预创订单参数
     * @return 预创订单响应结果
     */
    default JudankeResponse<OrderCreateResp> preCreate(OrderCreateParam param) {
        return request(param, new TypeReference<>() {
        });
    }

    /**
     * <a href="https://open.judanke.cn/docs/130/104">预取消订单，查询取消时候是否需要扣除违约金（实际扣费请已取消接口返回为准）</a>
     * 只能在待接单、取货中才能取消配送订单，其它情况下，请联系骑手取消
     *
     * @param param 预创订单参数
     * @return 预创订单响应结果
     */
    default JudankeResponse<PreCancelResp> preCancel(PreCancelParam param) {
        return request(param, new TypeReference<>() {
        });
    }


    /**
     * <a href="https://open.judanke.cn/docs/130/86">创建订单（并发单）</a>
     *
     * @param param 创建订单参数
     * @return 订单创建结果
     */
    default JudankeResponse<OrderCreateResp> publish(OrderCreateParam param) {
        return request(param, new TypeReference<>() {
        });
    }


    /**
     * <a href="https://open.judanke.cn/docs/130/102">新版获取报价（根据订单ID、支持自运力）</a>
     *
     * @param param 获取报价参数
     * @return 报价结果
     */
    default JudankeResponse<NewThirdQuotationResp> newThirdQuotation(NewThirdQuotationParam param) {
        return this.request(param, new TypeReference<>() {
        });
    }

    /**
     * <a href="https://open.judanke.cn/docs/130/70">
     * 已有的订单发起配送（如：同步过来的订单，已经创建过的订单创建时未曾发起配送）
     * 如果需要指定运力类型，请传递 delivery_extend ，
     * 参数为 {"ddks":{"delivery_type":1},"ss":{"delivery_type":0}}）
     * </a>
     *
     * @param param 获取报价参数
     * @return 报价结果
     */
    default JudankeResponse<OrderCreateResp> thirdPublish(ThirdPublishParam param) {
        return this.request(param, new TypeReference<>() {
        });
    }

    /**
     * <a href="https://open.judanke.cn/docs/130/28">
     * 发单后，一段时间内没骑手接单，追加一笔小费，吸引骑手接单
     * </a>
     *
     * @param param 获取报价参数
     * @return 报价结果
     */
    default JudankeResponse<Void> addTips(ThirdPublishParam param) {
        return this.request(param, new TypeReference<>() {
        });
    }


    @Override
    default String apiPrefix() {
        return "/Order/";
    }
}
