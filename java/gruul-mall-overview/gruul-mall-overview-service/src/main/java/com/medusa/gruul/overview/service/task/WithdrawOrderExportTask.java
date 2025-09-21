package com.medusa.gruul.overview.service.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.medusa.gruul.overview.api.enums.OwnerType;
import com.medusa.gruul.overview.api.enums.WithdrawOrderStatus;
import com.medusa.gruul.overview.api.enums.WithdrawType;
import com.medusa.gruul.overview.api.model.DrawTypeModel;
import com.medusa.gruul.overview.api.model.WithdrawalOrderExportDTO;
import com.medusa.gruul.overview.service.model.dto.PlatformWithdrawQueryDTO;
import com.medusa.gruul.overview.service.modules.export.service.IDataExportRecordManageService;
import com.medusa.gruul.overview.service.modules.withdraw.service.WithdrawService;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

/**
 * 提现工单导出任务
 *
 * @author jipeng
 * date 2024/2/2
 */
@Slf4j
@RequiredArgsConstructor
public class WithdrawOrderExportTask implements Runnable, BaseExportTask {

    private final static String TEMP_EXCEL_FILE_PATH = "/temp/提现工单.xlsx";
    private final Long recodeId;
    private final WithdrawService withdrawService;
    private final IDataExportRecordManageService dataExportRecordManageService;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    private final PlatformWithdrawQueryDTO query;

    private final static String TEMP_PATH = "/提现工单_%s.xlsx";

    @Override
    public void run() {
        String basePath = String.format(TEMP_PATH, IdUtil.fastUUID());
        File tempExcel = generateTempExcelFile(basePath);
        taskRun(tempExcel);
    }

    @Override
    public int generateExcel(File tempExcelFile) {
        AtomicInteger counter = new AtomicInteger(CommonPool.NUMBER_ONE);
        try (ExcelWriter excelWriter = EasyExcel.
                write(tempExcelFile, WithdrawalOrderExportDTO.class)
                .withTemplate(new ClassPathResource(getTaskTempExcelFilePath()).getInputStream())
                .needHead(Boolean.FALSE)
                .build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet(SHEET_NAME).build();
            while (true) {
                IPage<OverviewWithdraw> page = withdrawService.orderPage(query);
                List<OverviewWithdraw> records = page.getRecords();
                if (CollectionUtil.isEmpty(records)) {
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

    private List<WithdrawalOrderExportDTO> data(List<OverviewWithdraw> records,
                                                AtomicInteger counter) {
        List<WithdrawalOrderExportDTO> list = Lists.newArrayList();
        for (OverviewWithdraw record : records) {
            WithdrawalOrderExportDTO dto = new WithdrawalOrderExportDTO();
            dto.setNo(String.valueOf(counter.getAndIncrement()));
            dto.setApplyNo(record.getNo());
            dto.setUserName(record.getOwnerName());
            dto.setMobile(record.getContract());
            dto.setAmount(dealPrice(record.getDrawType().getAmount()));
            dto.setWithdrawTo(dealDrawTo(record.getDrawType()));
            dto.setUserType(dealOwnerType(record.getOwnerType()));
            dto.setStatus(dealWithDrawStatus(record.getStatus()));
            dto.setPaymentWay(record.getOffline()?"线下打款" : "线上打款");
            dto.setPaymentTime(record.getTradeTime());
            dto.setFailReason(record.getFailReason());
            dto.setRejectReason(record.getReason());
            dto.setApplyTime(DateUtil.format(record.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN));
            dto.setAuditUserName(record.getAuditUserName());
            dto.setAuditUserPhone(record.getAuditUserPhone());
            dto.setAuditTime(DateUtil.format(record.getAuditTime(), DatePattern.NORM_DATETIME_PATTERN));
            dto.setRemark(record.getRemark());
            list.add(dto);
        }
        return list;
    }

    private String dealWithDrawStatus(WithdrawOrderStatus status) {
        return switch (status) {
            case SUCCESS -> "已打款";
            case FORBIDDEN -> "已拒绝";
            case CLOSED -> "提现失败";
            case APPLYING -> "待审核";
            case PROCESSING -> "提现中";
        };
    }

    /**
     * 提现类型
     *
     * @param ownerType 提现类型
     * @return 提现类型文案
     */
    private String dealOwnerType(OwnerType ownerType) {
        return switch (ownerType) {
            case SHOP -> "交易提现(店铺)";
            case DISTRIBUTOR -> "佣金提现(分销商)";
            case SUPPLIER -> "交易提现(供应商)";
            case REBATE -> "返利提现(用户)";
        };
    }

    /**
     * 处理提现类型
     *
     * @param drawTypeModel 提现类型
     * @return 提现类型文案
     */
    private String dealDrawTo(DrawTypeModel drawTypeModel) {
        WithdrawType type = drawTypeModel.getType();
        if (WithdrawType.WECHAT.equals(type)) {
            return "微信";
        }
        if (WithdrawType.ALIPAY.equals(type)) {
            return String.format("姓名:%s,支付宝账号:%s", drawTypeModel.getName(),
                    drawTypeModel.getAlipayAccount());
        }
        if (WithdrawType.BANK_CARD.equals(type)) {
            return String.format("持卡人:%s,开户行:%s,银行卡号:%s",
                    drawTypeModel.getName(), drawTypeModel.getBank(), drawTypeModel.getCardNo());
        }
        return StringUtils.EMPTY;
    }

    @Override
    public PigeonChatStatisticsRpcService getPigeonChatStatisticsRpcService() {
        return pigeonChatStatisticsRpcService;
    }

    @Override
    public String getTaskTempExcelFilePath() {
        return TEMP_EXCEL_FILE_PATH;
    }


    @Override
    public IDataExportRecordManageService getDataExportRecordManageService() {
        return dataExportRecordManageService;
    }

    @Override
    public Long getDataExportDataId() {
        return recodeId;
    }
}
