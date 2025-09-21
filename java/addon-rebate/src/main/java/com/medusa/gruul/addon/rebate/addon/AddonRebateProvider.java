package com.medusa.gruul.addon.rebate.addon;


import com.medusa.gruul.goods.api.model.dto.EstimateRebateDTO;
import com.medusa.gruul.goods.api.model.param.EarningParam;
import com.medusa.gruul.order.api.addon.rebate.RebatePayParam;
import com.medusa.gruul.order.api.addon.rebate.RebatePayResponse;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import java.util.List;

/**
 * 返利插件提供器
 *
 * @author jinbu
 */
public interface AddonRebateProvider {

    String FILTER = "REBATE";

    /**
     * 使用返利支付生成返利支付折扣
     *
     * @param payParam 返利支付参数
     * @return 返利支付响应数据
     * @author 张治保
     */
    RebatePayResponse rebateDiscount(RebatePayParam payParam);

    /**
     * 返利预计赚
     *
     * @param param 预计赚请求参数
     * @return 预计赚金额
     */

    Long getProductRebateAmount(EstimateRebateDTO estimate);

    /**
     * 更新消费返利的订单信息
     * @param shopOrderItems
     * @param orderNo 订单号
     * @return 是否更新成功
     */
    Boolean updateRebateOrderInfo( List<ShopOrderItem> shopOrderItems,String orderNo);

    Long earning(EarningParam param);

}
