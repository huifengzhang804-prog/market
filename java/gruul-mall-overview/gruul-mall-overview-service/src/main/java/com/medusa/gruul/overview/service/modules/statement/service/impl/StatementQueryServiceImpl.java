package com.medusa.gruul.overview.service.modules.statement.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import com.medusa.gruul.overview.api.enums.SourceEnum;
import com.medusa.gruul.overview.service.model.StatementCountModel;
import com.medusa.gruul.overview.service.model.dto.OverviewStatementQueryDTO;
import com.medusa.gruul.overview.service.model.vo.OverviewStatementPageVO;
import com.medusa.gruul.overview.service.model.vo.StatementStatisticsVO;
import com.medusa.gruul.overview.service.modules.export.service.IDataExportRecordManageService;
import com.medusa.gruul.overview.service.modules.statement.service.StatementQueryService;
import com.medusa.gruul.overview.service.mp.statement.service.IOverviewStatementService;
import com.medusa.gruul.overview.service.task.StatementExportTask;

import java.util.Objects;
import java.util.concurrent.Executor;

import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author WuDi date 2022/10/10
 */
@Service
@RequiredArgsConstructor
public class StatementQueryServiceImpl implements StatementQueryService {


  private final IOverviewStatementService statementService;
  private final IDataExportRecordManageService dataExportRecordManageService;


  private final Executor globalExecutor;
  private final ShopRpcService shopRpcService;


  private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;

  /**
   * 分页查询对账单
   *
   * @param query 查询参数
   * @return 对账单
   */
  @Override
  public OverviewStatementPageVO queryStatement(OverviewStatementQueryDTO query) {

    //分页查询结果
    OverviewStatementPageVO result = OverviewStatementPageVO.toOverviewStatementPage(
        statementService.queryStatement(query));
    //统计
    List<StatementCountModel> statisticsList;
    if (result.getTotal() == 0 || CollUtil.isEmpty(
        statisticsList = statementService.statistics(query))) {
      return result.setStatistics(new StatementStatisticsVO());
    }
    StatementStatisticsVO statistics = new StatementStatisticsVO();
    for (StatementCountModel model : statisticsList) {
      if (ChangeType.INCREASE == model.getChangeType()) {
        statistics.setIncome(model.getAmount()).setIncomeCount(model.getAmountCount());
        continue;
      }
      statistics.setPayout(model.getAmount()).setPayoutCount(model.getAmountCount());
    }
    return result.setStatistics(statistics);
  }

  /**
   * 对账单导出功能
   *
   * @param query 查询DTO
   * @param source 来源
   * @return 导出记录id
   */
  @Override
  public Long export(OverviewStatementQueryDTO query, SourceEnum source) {
    SecureUser secureUser = ISecurity.userMust();
    Long exportRecord = dataExportRecordManageService.saveDataExportRecord(secureUser.getId(),
        secureUser.getShopId(), secureUser.getMobile(), ExportDataType.STATEMENT_OF_ACCOUNT);
    StatementExportTask statementExportTask=
        new StatementExportTask(query,this,
            dataExportRecordManageService,source,pigeonChatStatisticsRpcService,exportRecord);

    globalExecutor.execute(statementExportTask);
    return exportRecord;
  }


}
