package com.medusa.gruul.payment.service.common.constant;

/**
 * @author xiaoq
 * @Description 微信支付常量
 * @date 2022-10-13 16:05
 */
public interface WxConst {
    String TRADE_TYPE = "trade_type";

    String SUCCESS = "SUCCESS";

    String FAIL = "FAIL";
    String RETURN_CODE = "return_code";
    String RETURN_MSG = "return_msg";
    /**
     * 业务结果代码
     */
    String RESULT_CODE = "result_code";
    String TRADE_STATE = "trade_state";
    String TRADE_MSG = "trade_state_desc";
    String OUT_TRADE_NO = "out_trade_no";

    /**
     * 错误代码
     */
    String ERR_CODE = "err_code";
    /**
     * 错误代码描述
     */
    String RESULT_MSG = "err_code_des";


    /**
     * 付款成功时间
     */
    String PAYMENT_TIME = "payment_time";

    /**
     * 付款成功，返回的微信付款单号
     */
    String PAYMENT_NO = "payment_no";

    /**
     * 系统异常 需要重试
     */
    String SYSTEM_ERROR = "SYSTEMERROR";

    interface TransferConst {
        /**
         * 转账状态
         */
        String BATCH_STATUS = "batch_status";

        String OUT_BATCH_NO = "out_batch_no";
        String CLOSE_REASON = "close_reason";
        String BATCH_STATUS_ACCEPTED = "ACCEPTED";
        String BATCH_STATUS_PROCESSING = "PROCESSING";
        String BATCH_STATUS_FINISH = "FINISHED";
        String BATCH_STATUS_CLOSED = "CLOSED";
    }

    interface TradeStatus {
        String SUCCESS = "SUCCESS";
        String REFUND = "REFUND";
        String NOTPAY = "NOTPAY";
        String CLOSED = "CLOSED";
        String REVOKED = "REVOKED";
        String USERPAYING = "USERPAYING";
        String PAYERROR = "PAYERROR";
        String ACCEPT = "ACCEPT";
    }

    interface Refund {
        String STATUS = "status";
        String REQ_INFO = "req_info";
        String REFUND_STATUS = "refund_status";
        String OUT_REFUND_NO = "out_refund_no";
        //退款状态
        String SUCCESS = "SUCCESS";
        String PROCESSING = "PROCESSING";
        String CLOSED = "CLOSED";
        String ERROR = "CHANGE";
        String CLOSE = "REFUNDCLOSE";

    }
}
