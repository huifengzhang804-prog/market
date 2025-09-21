package com.medusa.gruul.overview.service.modules.statement.service;

import com.medusa.gruul.overview.api.enums.SourceEnum;
import com.medusa.gruul.overview.service.model.dto.OverviewStatementQueryDTO;
import com.medusa.gruul.overview.service.model.vo.OverviewStatementPageVO;

/**
 * @author: WuDi
 * @date: 2022/10/10
 */
public interface StatementQueryService {

    /**
     * 分页查询对账单
     *
     * @param statementQuery 查询参数
     * @return 对账单
     */
    OverviewStatementPageVO queryStatement(OverviewStatementQueryDTO statementQuery);

    /**
     * 对账单导出功能
     *
     * @param query 查询DTO
     * @param source 来源
     * @return 导出记录id
     */
    Long export(OverviewStatementQueryDTO query, SourceEnum source);
}
