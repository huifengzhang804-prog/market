package com.medusa.gruul.overview.api.rpc;

import cn.hutool.json.JSONObject;
import com.medusa.gruul.overview.api.enums.DrawType;
import com.medusa.gruul.overview.api.model.PurchaseOrderReq;
import com.medusa.gruul.overview.api.model.ShopBalanceVO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/11/30
 */
public interface OverviewRpcService {


    /**
     * 根据店铺id查询店铺余额详情
     *
     * @param shopIds 店铺id列表
     * @return key 店铺id value 店铺余额详情
     */
    Map<Long, ShopBalanceVO> getShopBalanceMap(@NotNull @Size(min = 1) Set<Long> shopIds);

    /**
     * 根据采购单生成对账单
     *
     * @param purchaseOrderReq 采购订单对象,参见{@link PurchaseOrderReq}
     * @return
     */
    void generateStatementOfPurchase(PurchaseOrderReq purchaseOrderReq);

    /**
     * 退还采购订单
     *
     * @param orderNo 采购订单订单号
     */
    void refundPurchaseOrder(@NotNull Long supplierShopId, @NotNull String orderNo);


    /**
     * 获取提现用户信息
     *
     * @param userId 用户id
     * @return 提现配置信息
     */
    Map<DrawType, JSONObject> getAccountsByUserId(Long userId);
}
