package com.medusa.gruul.overview.service.mp.statement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.overview.api.entity.OverviewStatement;
import com.medusa.gruul.overview.service.model.StatementCountModel;
import com.medusa.gruul.overview.service.model.dto.OverviewStatementQueryDTO;
import com.medusa.gruul.overview.service.model.dto.PurchasePayoutQueryDTO;
import com.medusa.gruul.overview.service.model.vo.PurchasePayoutVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 对账单 Mapper 接口
 * </p>
 *
 * @author WuDi
 * @since 2022-10-09
 */
public interface OverviewStatementMapper extends BaseMapper<OverviewStatement> {


    /**
     * 根据查询条件分页查询对账单
     *
     * @param page  分页条件
     * @param query 查询条件
     * @return 查询结果
     */
    IPage<OverviewStatement> queryStatement(@Param("page") IPage<OverviewStatement> page, @Param("query") OverviewStatementQueryDTO query);


    /**
     * 查询统计
     *
     * @param query 查询条件
     * @return 统计结果
     */
    List<StatementCountModel> statistics(@Param("query") OverviewStatementQueryDTO query);

    /**
     * 分页查询采购支出
     * @param query {@link PurchasePayoutQueryDTO}
     * @return {@link PurchasePayoutVO}
     */
    IPage<PurchasePayoutVO> queryPurchasePayoutList(@Param("query") PurchasePayoutQueryDTO query);
}
