package com.medusa.gruul.overview.service.modules.withdraw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.medusa.gruul.overview.api.model.WithdrawOrderDTO;
import com.medusa.gruul.overview.service.model.dto.*;

/**
 * @author 张治保
 * date 2022/11/21
 */
public interface WithdrawService {

    /**
     * 提现工单申请
     *
     * @param shopId   店铺id
     * @param withdraw 提现参数
     */
    void newWithdraw(Long shopId, ShopWithdrawDTO withdraw);

    /**
     * 创建提现工单
     *
     * @param msgId         消息id 用作幂等性校验
     * @param withdrawOrder 提现工单详情
     */
    void createOrder(String msgId, WithdrawOrderDTO withdrawOrder);

    /**
     * 分页查询提现工单
     *
     * @param query 查询条件
     * @return 查询结果
     */
    IPage<OverviewWithdraw> orderPage(PlatformWithdrawQueryDTO query);

    /**
     * 根据订单好审核提现工单
     *
     * @param orderNo 订单号
     * @param audit   审核结果
     */
    void orderAuditByNo(String orderNo, WithdrawAuditDTO audit);

    /**
     * 根据用户id分页查询用户提现工单
     *
     * @param userId 用户id
     * @param query  分页查询条件
     * @return 分页查询结果
     */
    IPage<OverviewWithdraw> distributeWithdrawPage(Long userId, WithdrawQuery query);


    /**
     * 店铺分页查询提现工单
     *
     * @param shopId 店铺id
     * @param query  查询条件
     * @return 分页查询结果
     */
    IPage<OverviewWithdraw> shopWithdrawPage(Long shopId, ShopWithdrawQueryDTO query);

    /**
     * 批量备注提现工单号
     *
     * @param withdrawRemark
     */
    void remarkBatch(WithdrawRemarkDTO withdrawRemark);

    /**
     *供应商结算导出
     * @param query 查询条件
     * @return 导出结果
     */
    Long export(ShopWithdrawQueryDTO query);

    /**
     * 导出提现工单
     * @param query 查询条件
     * @return 导出id
     */
    Long exportWithDrawOrder(PlatformWithdrawQueryDTO query);

    /**
     * 更新提现工单的状态
     * @param orderNo 工单号
     * @param status 工单状态
     *
     */
    void updateWithdrawOrderStatus(String orderNo, String status,String closeReason);
}
