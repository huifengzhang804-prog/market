package com.medusa.gruul.afs.service.mq;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/8
 */
public interface AfsQueueNames {
    /**
     * 系统自动同意售后申请队列名
     */
    String AFS_AUTO_AGREE_REQUEST_QUEUE ="afs.auto.agreeRequest";

    /**
     *  系统自动确认收到退货队列名
     */
    String AFS_AUTO_CONFIRM_RETURNED_QUEUE = "afs.auto.confirmReturned";

    /**
     * 系统自动关闭售后队列名
     */
    String AFS_AUTO_CLOSE_QUEUE = "afs.auto.close";

    /**
     * 退款成功回调 队列
     */
    String AFS_REFUNDED_QUEUE = "afs.refunded.succeed";

}
