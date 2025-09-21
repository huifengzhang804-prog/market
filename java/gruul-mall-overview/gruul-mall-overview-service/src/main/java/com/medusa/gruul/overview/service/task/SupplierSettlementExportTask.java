package com.medusa.gruul.overview.service.task;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.medusa.gruul.overview.api.enums.WithdrawOrderStatus;
import com.medusa.gruul.overview.api.model.SupplierSettlementExportDTO;
import com.medusa.gruul.overview.service.model.dto.ShopWithdrawQueryDTO;
import com.medusa.gruul.overview.service.modules.base.service.ShopBalanceManagerService;
import com.medusa.gruul.overview.service.modules.export.service.IDataExportRecordManageService;
import com.medusa.gruul.overview.service.modules.withdraw.service.WithdrawService;
import com.medusa.gruul.overview.service.mp.base.entity.OverviewShopBalance;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 供应商结算导出任务
 *
 * @author jipeng
 * @date 2024/2/2
 */
@Slf4j
@RequiredArgsConstructor
public class SupplierSettlementExportTask implements Runnable, BaseExportTask {


  String TEMP_EXCEL_FILE_PATH = "/temp/供应商结算.xlsx";

  private final WithdrawService withdrawService;
  private final Long shopId;
  private final ShopWithdrawQueryDTO query;
  private final Long recodeId;
  private final IDataExportRecordManageService dataExportRecordManageService;
  private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
  private final ShopBalanceManagerService shopBalanceManagerService;
  private final UaaRpcService uaaRpcService;


  @Override
  public void run() {

    String tempPath = "/供应商结算_%s.xlsx";
    String basePath = String.format(tempPath, IdUtil.fastUUID());
    File tempExcel = generateTempExcelFile(basePath);
    taskRun(tempExcel);

  }

  @Override
  public int generateExcel(File tempExcel) {

    AtomicInteger counter = new AtomicInteger(CommonPool.NUMBER_ONE);
    try (ExcelWriter excelWriter = EasyExcel.
        write(tempExcel, SupplierSettlementExportDTO.class)
        .withTemplate(new ClassPathResource(getTaskTempExcelFilePath()).getInputStream())
        .needHead(Boolean.FALSE)
        .build()) {
      Map<String, String> fillData = Maps.newHashMap();
      OverviewShopBalance overviewShopBalance = shopBalanceManagerService.get(shopId);
      fillData.put("undrawn", dealPrice(overviewShopBalance.getUndrawn()));
      fillData.put("withdrawing",
              dealPrice(overviewShopBalance.getWithdrawing()));
      fillData.put("withdrawalTotal", dealPrice(overviewShopBalance.getWithdrawalTotal()));
      WriteSheet writeSheet = EasyExcel.writerSheet(SHEET_NAME).build();

      //写入统计数据
      excelWriter.fill(fillData, writeSheet);
      while (true) {
        IPage<OverviewWithdraw> page = withdrawService.shopWithdrawPage(shopId,
            query);
        List<OverviewWithdraw> records = page.getRecords();
        if (records.size() == CommonPool.NUMBER_ZERO) {
          break;
        }
        query.setCurrent(query.getCurrent() + CommonPool.NUMBER_ONE);
        excelWriter.write(data(records, counter), writeSheet);
      }

    } catch (IOException e) {

      throw new RuntimeException(e);
    }
    return counter.get() - CommonPool.NUMBER_ONE;

  }

  @Override
  public IDataExportRecordManageService getDataExportRecordManageService() {
    return dataExportRecordManageService;
  }

  @Override
  public Long getDataExportDataId() {
    return recodeId;
  }

  /**
   * 导出数据处理
   *
   * @param records 数据库中数据
   * @param counter 计数器
   * @return 处理后的数据
   */
  private List<SupplierSettlementExportDTO> data(List<OverviewWithdraw> records,
      AtomicInteger counter) {
    List<SupplierSettlementExportDTO> result = Lists.newArrayListWithCapacity(records.size());
    Set<Long> applyUserIds = records.stream().map(OverviewWithdraw::getApplyUserId).collect(Collectors.toSet());
    Map<Long, UserInfoVO> userInfoVOMap = uaaRpcService.getUserDataBatchByUserIds(applyUserIds);
    for (OverviewWithdraw record : records) {
      SupplierSettlementExportDTO dto = new SupplierSettlementExportDTO();
      dto.setIndex(String.valueOf(counter.getAndIncrement()));
      dto.setTradeNo(record.getNo());
      dto.setAmount(dealPrice(record.getDrawType().getAmount()));
      //状态
      dto.setStatus(dealStatus(record.getStatus()));
      //申请时间
      dto.setApplyTime(record.getCreateTime()
          .format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
      //审批时间
      dto.setApproveTime(record.getUpdateTime()
          .format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
      //拒绝说明
      dto.setRejectReason(record.getReason());
      dto.setApplyUserPhone(record.getApplyUserPhone());
      UserInfoVO userInfoVO = userInfoVOMap.get(record.getApplyUserId());
      dto.setApplyUserNickName(Optional.ofNullable(userInfoVO).orElseGet(UserInfoVO::new).getNickname());

      result.add(dto);

    }
    return result;

  }

  /**
   * 状态处理
   *
   * @param status 状态
   * @return 状态的文案
   */
  private String dealStatus(WithdrawOrderStatus status) {

    if (Objects.isNull(status)) {
      return StringUtils.EMPTY;
    }
    return switch (status) {
      case FORBIDDEN -> "已拒绝";
      case SUCCESS -> "已到账";
      case APPLYING -> "待审核";
      case CLOSED -> "已关闭";
      case PROCESSING -> "进行中";
    };
  }

  /**
   * 获取上传RPC服务
   *
   * @return 上传RPCService
   */
  @Override
  public PigeonChatStatisticsRpcService getPigeonChatStatisticsRpcService() {
    return pigeonChatStatisticsRpcService;
  }

  /**
   * 获取临时文件路径
   *
   * @return 临时文件路径
   */
  @Override
  public String getTaskTempExcelFilePath() {
    return TEMP_EXCEL_FILE_PATH;
  }


}
