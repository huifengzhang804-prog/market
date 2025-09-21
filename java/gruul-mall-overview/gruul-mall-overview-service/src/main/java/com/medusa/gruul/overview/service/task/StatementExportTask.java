package com.medusa.gruul.overview.service.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.collect.Lists;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.enums.TransactionType;
import com.medusa.gruul.overview.api.entity.OverviewStatement;
import com.medusa.gruul.overview.api.enums.SourceEnum;
import com.medusa.gruul.overview.api.model.PlateStatementExportDTO;
import com.medusa.gruul.overview.api.model.ShopStatementExportDTO;
import com.medusa.gruul.overview.service.model.dto.OverviewStatementQueryDTO;
import com.medusa.gruul.overview.service.model.vo.OverviewStatementPageVO;
import com.medusa.gruul.overview.service.modules.export.service.IDataExportRecordManageService;
import com.medusa.gruul.overview.service.modules.statement.service.StatementQueryService;

import java.io.File;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

/**
 * 对账单导出任务
 *
 * @author jipeng
 * @date 2024/2/2
 */
@RequiredArgsConstructor
@Slf4j
public class StatementExportTask implements Runnable, BaseExportTask {


    /**
     * 对账单的查询条件
     */
    private final OverviewStatementQueryDTO overviewStatementQueryDTO;

    private final StatementQueryService statementQueryService;

    private final IDataExportRecordManageService dataExportRecordManageService;

    private final SourceEnum sourceEnum;

    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;


    /**
     * 导出记录id
     */

    private final Long exportRecord;

    @Override
    public void run() {

        String tempPath = "/对账单_%s.xlsx";
        String basePath = String.format(tempPath, IdUtil.fastUUID());
        File tempExcelFile = generateTempExcelFile(basePath);
        taskRun(tempExcelFile);
    }

    /**
     * 生成EXCEl
     *
     * @param tempExcelFile 用于临时生成的文件
     * @return 生成的记录数
     */
    @Override
    public int generateExcel(File tempExcelFile) {

        ExcelWriter excelWriter = null;
        AtomicInteger counter = new AtomicInteger(CommonPool.NUMBER_ONE);
        try {
            if (SourceEnum.PLATFORM.equals(sourceEnum)) {
                excelWriter = EasyExcel.
                        write(tempExcelFile, PlateStatementExportDTO.class)
                        .withTemplate(getTemplateStream()).needHead(Boolean.FALSE)
                        .build();
            } else {
                excelWriter = EasyExcel.
                        write(tempExcelFile, ShopStatementExportDTO.class)
                        .withTemplate(getTemplateStream()).needHead(Boolean.FALSE)
                        .build();
            }
            WriteSheet writeSheet = EasyExcel.writerSheet(SHEET_NAME).build();
            while (true) {
                OverviewStatementPageVO page = statementQueryService.queryStatement(
                        overviewStatementQueryDTO);
                List<OverviewStatement> records = page.getRecords();
                if (records.size() == CommonPool.NUMBER_ZERO) {
                    break;
                }
                overviewStatementQueryDTO.getPage()
                        .setCurrent(overviewStatementQueryDTO.getPage().getCurrent() + CommonPool.NUMBER_ONE);
                List<PlateStatementExportDTO> data = data(records, counter);
                if (SourceEnum.PLATFORM.equals(sourceEnum)) {
                    excelWriter.write(data, writeSheet);
                } else {
                    //店铺和供应商导出的数据
                    List<ShopStatementExportDTO> shopStatementExportDTOS = BeanUtil.copyToList(data,
                            ShopStatementExportDTO.class);
                    excelWriter.write(shopStatementExportDTOS, writeSheet);
                }
            }
        } finally {
            if (Objects.nonNull(excelWriter)) {
                excelWriter.close();
            }
        }
        return counter.get() - CommonPool.NUMBER_ONE;
    }

    /**
     * 列表数据处理
     *
     * @param records 数据库中数据
     * @param counter 计数器
     * @return 处理后的数据
     */
    private List<PlateStatementExportDTO> data(List<OverviewStatement> records,
                                               AtomicInteger counter) {
        List<PlateStatementExportDTO> result = Lists.newArrayListWithCapacity(records.size());
        for (OverviewStatement record : records) {
            PlateStatementExportDTO dto = new PlateStatementExportDTO();
            dealItem(counter, record, dto);
            result.add(dto);
        }
        return result;
    }

    /**
     * 处理单条数据
     *
     * @param counter 计数器
     * @param record  数据库中单条数据
     * @param dto     导出后的数据
     */
    private void dealItem(AtomicInteger counter, OverviewStatement record,
                          PlateStatementExportDTO dto) {
        //编号、
        dto.setIndex(String.valueOf(counter.getAndIncrement()));
        //  交易流水号、
        dto.setTradeNo(record.getTradeNo());
        //订单号
        dto.setOrderNo(record.getOrderNo());
        //  交易类型、
        dto.setTradeType(dealTradeType(record.getTradeType()));
        //收支金额
        if (ChangeType.INCREASE.equals(record.getChangeType())) {
            dto.setAmount("+" + dealPrice(record.getAmount()));
        } else {
            dto.setAmount("-" + dealPrice(record.getAmount()));
        }

        //所属商家
        dto.setShopName(record.getShopName());
        //交易时间
        dto.setTradeTime(record.getTradeTime()
                .format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
    }


    /**
     * 交易类型
     *
     * @param tradeType 交易类型
     * @return 交易类型对应的文案
     */
    private String dealTradeType(TransactionType tradeType) {
        if (Objects.isNull(tradeType)) {
            return StringUtils.EMPTY;
        }
        //供应商端的文案不同
        if (SourceEnum.SUPPLIER.equals(sourceEnum)) {
            return switch (tradeType) {
                case USER_BALANCE_RECHARGE -> "会员余额充值";
                case PAID_MEMBER_OPEN -> "付费会员开通";
                case PAID_MEMBER_RENEW -> "付费会员续费";
                case INTEGRAL_GOODS_DEAL -> "积分商品交易";
                case SYSTEM_SERVICE -> "平台服务费(代销)";
                case MEMBER_DISCOUNT -> "会员抵扣";
                case MEMBER_LOGISTICS_DISCOUNT -> "会员包邮";
                case PLATFORM_DISCOUNT_COUPON -> "平台优惠券";
                case REBATE_DEDUCTION -> "返利抵扣";
                case REBATE_GIVE -> "返利赠送";
                case ORDER_PAID -> "代销交易";
                case ORDER_FREIGHT -> "代销运费";
                case DISTRIBUTE -> "分销佣金";
                case PURCHASE_ORDER -> "采购交易";
                case PURCHASE_ORDER_FREIGHT -> "采购运费";
                case PURCHASE_ORDER_SERVICE_CHARGE -> "平台服务费(采购)";
                case CONSIGNMENT_DISBURSE -> "代销支出";
                case RECHARGE_GIFT -> "充值赠送";
            };
        }
        return switch (tradeType) {
            case USER_BALANCE_RECHARGE -> "用户充值";
            case PAID_MEMBER_OPEN -> "购买付费会员";
            case PAID_MEMBER_RENEW -> "续费付费会员";
            case INTEGRAL_GOODS_DEAL -> "积分商品交易";
            case SYSTEM_SERVICE -> "平台服务费";
            case MEMBER_DISCOUNT -> "会员折扣";
            case MEMBER_LOGISTICS_DISCOUNT -> "会员包邮";
            case PLATFORM_DISCOUNT_COUPON -> "平台优惠券";
            case REBATE_DEDUCTION -> "返利抵扣";
            case REBATE_GIVE -> "返利赠送";
            case ORDER_PAID -> "商品交易";
            case ORDER_FREIGHT -> "订单运费";
            case DISTRIBUTE -> "分销佣金";
            case PURCHASE_ORDER -> "采购交易";
            case PURCHASE_ORDER_FREIGHT -> "采购运费";
            case PURCHASE_ORDER_SERVICE_CHARGE -> "平台服务费(采购)";
            case CONSIGNMENT_DISBURSE -> "代销支出";
            case RECHARGE_GIFT -> "充值赠送";
        };
    }


    @Override
    public PigeonChatStatisticsRpcService getPigeonChatStatisticsRpcService() {
        return pigeonChatStatisticsRpcService;
    }

    @Override
    public String getTaskTempExcelFilePath() {
        return getFilePathBySource(sourceEnum);
    }

    @Override
    public IDataExportRecordManageService getDataExportRecordManageService() {
        return dataExportRecordManageService;
    }

    /**
     * 获取记录id
     *
     * @return 记录id
     */
    @Override
    public Long getDataExportDataId() {
        return exportRecord;
    }


    /**
     * 获取模板的数据流
     *
     * @return 模板数据流
     */
    private InputStream getTemplateStream() {
        InputStream inputStream = null;
        try {
            inputStream = new ClassPathResource(getFilePathBySource(sourceEnum)).getInputStream();
        } catch (Exception e) {
            log.error("获取模板失败", e);
        }
        return inputStream;
    }

    /**
     * 根据来源类型获取不同的模板
     *
     * @param sourceEnum 来源类型
     * @return 模板路径
     */
    private static String getFilePathBySource(SourceEnum sourceEnum) {
        return switch (sourceEnum) {
            case SHOP -> "temp/对账单_商家端.xlsx";
            case SUPPLIER -> "temp/对账单_供应商.xlsx";
            default -> "temp/对账单_平台端.xlsx";
        };
    }
}
