package com.medusa.gruul.addon.ic.modules.opens.judanke;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/6
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class JudankeConfig {

    /**
     * <a href="https://jop.judanke.cn">请求域名</a>
     * ⽣产环境订单会真实发单并指派骑⼿且真实扣费。根据预先配置的回调地址进⾏订单状态流转信息推送
     */
    private static final String DOMAIN = "https://jop.judanke.cn";
    /**
     * <a href="https://joptest.judanke.cn">请求域名</a>
     * <p>
     * 测试环境订单不会真实发单
     * 后续订单状态流转推送需要在开放平台⼿动模拟触发（<a href="https://open.judanke.cn/tools/order/flow">相关地址</a>）
     * 模拟回调会正常执⾏订单业务流程，并且作⽤于此订单相关数据（订单列表及详情数据）
     * 模拟回调只能对已发单且符合当前回调状态的订单进⾏回调（例如：回调过「已完成」状态后⽆法再次回调「配送中」状态）
     */
    private static final String DOMAIN_TEST = "https://joptest.judanke.cn";

    private boolean test = false;

    private String userId = "100688";

    private String apiKey = "e4833da2d8815690ce9d01b7eb2aae4baeaf3609";


    public String getDomain() {
        return isTest() ? DOMAIN_TEST : DOMAIN;
    }

}
