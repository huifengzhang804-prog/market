package com.medusa.gruul.afs.service.service;

import com.medusa.gruul.afs.api.enums.RefundType;
import com.medusa.gruul.afs.api.model.AfsCloseDTO;
import com.medusa.gruul.afs.service.model.dto.AfsAuditDTO;
import com.medusa.gruul.afs.service.model.dto.AfsPageDTO;
import com.medusa.gruul.afs.service.model.dto.AfsRemarkDTO;
import com.medusa.gruul.afs.service.model.dto.BuyerReturnedDTO;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/6
 */
public interface AfsService {


    /**
     * 商家同意售后申请
     *
     * @param afsNo    售后工单号
     * @param afsAudit 审核结果
     * @param isSystem 是否是系统自动操作
     */
    void afsRequestAgreeReject(String afsNo, AfsAuditDTO afsAudit, boolean isSystem);


    /**
     * 退款成功回调
     *
     * @param afsNo     售后工单号
     * @param refundNum 退款单号
     */
    void refundedNotify(String afsNo, String refundNum);

    /**
     * 买家已退货 退货退款
     *
     * @param afsNo         售后工单号
     * @param type          退货类型
     * @param buyerReturned 用户退货信息
     */
    void afsBuyerReturned(String afsNo, RefundType type, BuyerReturnedDTO buyerReturned);

    /**
     * 商家确认/拒绝收货
     *
     * @param afsNo    售后工单号
     * @param afsAudit 审核信息
     * @param isSystem 是否系统操作
     */
    void afsReturnedConfirmReject(String afsNo, AfsAuditDTO afsAudit, boolean isSystem);

    /**
     * 买家关闭售后
     *
     * @param afsClose 售后关闭数据详情
     */
    void afsClose(AfsCloseDTO afsClose);

    /**
     * 售后工单批量备注
     *
     * @param afsRemark 售后工单备注参数
     */
    void afsOrderBatchRemark(AfsRemarkDTO afsRemark);

    /**
     * 售后工单导出
     * @param afsPage 导出参数
     * @return 导出id
     */
  Long export(AfsPageDTO afsPage);
}
