package com.medusa.gruul.payment.service.common.constant;

/**
 * Ali 交易常量
 *
 * @author 张治保
 * date 2021/12/22
 */
public interface AliConst {

    String SUCCESS_STATUS = "SUCCESS";
    String CODE = "code";
    String SUCCESS_CODE = "10000";
    String MSG = "msg";
    String TRADE_STATUS = "trade_status";
    String SUB_CODE = "sub_code";
    String SUB_CODE_SUCCESS = "ACQ.TRADE_HAS_SUCCESS";
    String SUB_MSG = "sub_msg";

    String TRADE_QUERY_KEY = "alipay_trade_query_response";
    String REFUND_SUCCESS = "REFUND_SUCCESS";

    /**
     * 支付宝转账响应数据 key
     */
    String ALIPAY_FUND_TRANS_UNI_TRANSFER_RESPONSE = "alipay_fund_trans_uni_transfer_response";
    /**
     * 转账单据状态。 SUCCESS（该笔转账交易成功）：成功； FAIL：失败（具体失败原因请参见error_code以及fail_reason返回值）；
     */
    String STATUS = "status";
    /**
     * 错误码字段名
     */
    String ERROR_CODE = "error_code";

    /**
     * 错误原因
     */
    String FAIL_REASON = "fail_reason";

    /**
     * 订单号
     */
    String ORDER_ID = "order_id";

    /**
     * 订单支付时间
     */
    String TRANS_DATE = "trans_date";

    /**
     * 支付宝支付资金流水号
     */
    String PAY_FUND_ORDER_ID = "pay_fund_order_id";

    /**
     *
     */
    String SYSTEM_ERROR_CODE = "SYSTEM_ERROR";

    /**
     * 交易状态：
     * WAIT_BUYER_PAY（交易创建，等待买家付款）、
     * TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、
     * TRADE_SUCCESS（交易支付成功）、
     * TRADE_FINISHED（交易结束，不可退款）
     */
    interface TradeStatus {
        String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_CLOSED = "TRADE_CLOSED";
        String TRADE_SUCCESS = "TRADE_SUCCESS";
        String TRADE_FINISHED = "TRADE_FINISHED";
    }

    interface Refund {
        String REFUND_FEE = "refund_fee";
        String GMT_REFUND = "gmt_refund";
        String OUT_BIZ_NO = "out_biz_no";
    }

}
