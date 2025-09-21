package com.medusa.gruul.overview.api.rpc;

/**
 * @author jipeng
 * @since 2024/11/7
 */
public interface WithdrawRpcService {
    /**
     * 更新提现工单的状态
     * @param orderNo
     * @param status
     */
    void  updateWithdrawOrderStatus(String orderNo,String status,String closeReason);
}
