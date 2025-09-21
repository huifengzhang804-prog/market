package com.medusa.gruul.overview.service.mp.statement.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.overview.api.entity.OverviewStatement;
import com.medusa.gruul.overview.service.model.StatementCountModel;
import com.medusa.gruul.overview.service.model.dto.OverviewStatementQueryDTO;
import com.medusa.gruul.overview.service.model.dto.PurchasePayoutQueryDTO;
import com.medusa.gruul.overview.service.model.vo.PurchasePayoutVO;

import java.util.List;

/**
 * <p>
 * 对账单 服务类
 * </p>
 *
 * @author WuDi
 * @since 2022-10-09
 */
public interface IOverviewStatementService extends IService<OverviewStatement> {

    /**
     * 根据查询条件分页查询对账单
     *
     * @param query 查询条件
     * @return 查询结果
     */
    IPage<OverviewStatement> queryStatement(OverviewStatementQueryDTO query);


    /**
     * 查询统计
     *
     * @param query 查询条件
     * @return 统计结果
     */
    List<StatementCountModel> statistics(OverviewStatementQueryDTO query);

    /**
     * 根据订单号获取采购单
     * @param orderNo 订单号
     * @return {@link OverviewStatement}
     */
    List<OverviewStatement> listOverviewStatementByOrderNo(String orderNo);

    /**
     * 分页查询采购支出
     * @param query {@link PurchasePayoutQueryDTO}
     * @return {@link PurchasePayoutVO}
     */
    IPage<PurchasePayoutVO> queryPurchasePayoutList(PurchasePayoutQueryDTO query);
}
