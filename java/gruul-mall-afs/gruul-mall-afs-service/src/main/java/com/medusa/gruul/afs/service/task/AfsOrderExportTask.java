package com.medusa.gruul.afs.service.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.medusa.gruul.afs.api.enums.AfsType;
import com.medusa.gruul.afs.service.model.dto.AfsOrderExportDTO;
import com.medusa.gruul.afs.service.model.dto.AfsPageDTO;
import com.medusa.gruul.afs.service.model.enums.AfsError;
import com.medusa.gruul.afs.service.mp.entity.AfsHistory;
import com.medusa.gruul.afs.service.mp.entity.AfsOrder;
import com.medusa.gruul.afs.service.service.AfsQueryService;
import com.medusa.gruul.carrier.pigeon.api.model.dto.UploadParamDTO;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import com.medusa.gruul.overview.api.enums.DataExportStatus;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import io.vavr.control.Option;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 售后工单导出任务
 *
 * @author jipeng
 * @date: 2024/2/2
 */
@Builder
@Slf4j
public class AfsOrderExportTask implements Runnable {

    /**
     * 模板文件路径
     */
    public static final String TEMP_FILE_PATH = "temp/售后工单_temp.xlsx";
    public static final String SEPARATOR = ",";
    /**
     * 一万
     */
    public static final Integer TEN_THOUSAND = 10000;
    private static final String SHEET_NAME = "SheetJS";
    private static String TEMP_PATH = "/售后工单_%s.xlsx";
    /**
     * 导出记录id
     */
    private Long exportRecordId;

    private AfsQueryService afsQueryService;

    private AfsPageDTO afsPageDTO;

    private UaaRpcService uaaRpcService;


    private PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;

    private DataExportRecordRpcService dataExportRecordRpcService;

    /**
     * 生成临时文件
     *
     * @return 临时文件
     */
    private static File generateTempExcelFile() {
        File generateFile = null;
        try (InputStream templateInputStream = new ClassPathResource(TEMP_FILE_PATH).getInputStream()) {
            // 复制资源文件到新的文件
            generateFile = new File(String.format(TEMP_PATH, IdUtil.fastUUID()));
            Files.copy(templateInputStream, generateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error("创建临时文件失败", e);
            throw AfsError.TEMP_AFS_ORDER_GENERATE_ERROR.exception();
        }
        return generateFile;
    }

    @Override
    public void run() {
        File tempExcelFile = generateTempExcelFile();
        DataExportRecordDTO update = new DataExportRecordDTO();
        update.setId(exportRecordId);
        try {
            int count = generateExcel(tempExcelFile);
            log.debug("订单导出成功,数据条数为:{}", count);
            //上传文件到OSS
            String path = uploadFileToOSS(tempExcelFile);
            update.setFilePath(path);
            update.setDataCount(count);
            update.setFileSize(Long.valueOf(tempExcelFile.length()).intValue());
            update.setStatus(DataExportStatus.SUCCESS);
        } catch (Exception e) {
            log.error("导出失败", e);
            //导出失败
            update.setStatus(DataExportStatus.FAILED);
        } finally {
            if (Objects.nonNull(tempExcelFile)) {
                FileUtil.del(tempExcelFile);
            }
            dataExportRecordRpcService.updateExportRecordData(update);
        }

    }

    /**
     * 导出Excel上传到OSS
     *
     * @param tempExcelFile 临时文件
     * @return 文件上传后的目录
     */
    private String uploadFileToOSS(File tempExcelFile) {
        try {
            Map<Integer, UploadParamDTO> uploadParamMap = Maps.newHashMapWithExpectedSize(
                    CommonPool.NUMBER_ONE);
            UploadParamDTO uploadParamDTO = new UploadParamDTO();
            try (InputStream inputStream = new FileInputStream(tempExcelFile)) {
                byte[] buffer = new byte[(int) tempExcelFile.length()];
                inputStream.read(buffer);
                uploadParamDTO.setFileBytes(buffer);
                uploadParamDTO.setFormat(FileNameUtil.extName(tempExcelFile));
            }
            uploadParamMap.put(CommonPool.NUMBER_ZERO, uploadParamDTO);
            Map<Integer, String> pathMap = pigeonChatStatisticsRpcService.batchUpload(
                    uploadParamMap);
            return pathMap.get(CommonPool.NUMBER_ZERO);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw AfsError.AFS_ORDER_UPLOADER_ERROR.exception();
        }
    }

    private int generateExcel(File tempExcelFile) {

        ExcelWriter excelWriter = null;
        AtomicInteger counter = new AtomicInteger(CommonPool.NUMBER_ONE);
        try {

            excelWriter = EasyExcel.
                    write(tempExcelFile, AfsOrderExportDTO.class)
                    .withTemplate(new ClassPathResource(TEMP_FILE_PATH).getInputStream())
                    .needHead(Boolean.FALSE)
                    .build();
            WriteSheet writeSheet = EasyExcel.writerSheet(SHEET_NAME).build();
            while (Boolean.TRUE) {
                IPage<AfsOrder> page = afsQueryService.afsOrderPage(afsPageDTO);
                List<AfsOrder> records = page.getRecords();
                if (records.size() == CommonPool.NUMBER_ZERO) {
                    break;
                }
                afsPageDTO.setCurrent(afsPageDTO.getCurrent() + CommonPool.NUMBER_ONE);
                excelWriter.write(data(records, counter), writeSheet);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(excelWriter)) {
                excelWriter.close();
            }
        }
        return counter.get() - CommonPool.NUMBER_ONE;
    }

    private List<AfsOrderExportDTO> data(List<AfsOrder> records, AtomicInteger counter) {
        List<AfsOrderExportDTO> result = Lists.newArrayListWithCapacity(records.size());
        //获取用户ids rpc查询用户信息
        Set<Long> userIds = records.stream().map(AfsOrder::getBuyerId).collect(Collectors.toSet());
        Map<Long, UserInfoVO> userDataMap = uaaRpcService.getUserDataBatchByUserIds(userIds);

        //获取工单号ids
        Set<String> noIds = records.stream().map(AfsOrder::getNo).collect(Collectors.toSet());
        Map<String, AfsHistory> afsHistoryMap = afsQueryService.lastAfsHistory(noIds);

        for (AfsOrder record : records) {
            AfsOrderExportDTO dto = dealItem(counter, record, userDataMap, afsHistoryMap);

            result.add(dto);
        }
        return result;
    }

    private AfsOrderExportDTO dealItem(AtomicInteger counter, AfsOrder record,
                                       Map<Long, UserInfoVO> userDataMap,
                                       Map<String, AfsHistory> afsHistoryMap) {
        AfsOrderExportDTO dto = new AfsOrderExportDTO();
        //  编号、
        dto.setIndex(String.valueOf(counter.getAndIncrement()));
        //  订单编号、
        dto.setOrderNo(record.getNo());
        //成交价
        dto.setDealPrice(dealPrice(record.getAfsOrderItem().getDealPrice()));
        // 买家昵称、
        dto.setBuyerNickname(record.getBuyerNickname());
        //买家手机号
        Option.of(userDataMap.get(record.getBuyerId())).peek(u -> {
            dto.setBuyerMobile(u.getMobile());
        });
        //退款商品名
        dto.setProductName(record.getAfsOrderItem().getProductName());
        // 商品编号(SPUID)
        dto.setProductId(record.getAfsOrderItem().getProductId().toString());
        //规格编号(SKUID)
        dto.setSkuId(record.getAfsOrderItem().getSkuId().toString());
        //规格、

        dto.setProductSpec(
                CollectionUtil.isEmpty(record.getAfsOrderItem().getSpecs()) ? StringUtils.EMPTY
                        : Joiner.on(SEPARATOR).join(record.getAfsOrderItem().getSpecs()));
        //退款数
        dto.setRefundCount(String.valueOf(record.getAfsOrderItem().getNum()));
        //退款金额
        dto.setRefundAmount(dealPrice(record.getRefundAmount()));
        //退款类型
        if (AfsType.REFUND.equals(record.getType())) {
            dto.setRefundType("仅退款");
        }
        if (AfsType.RETURN_REFUND.equals(record.getType())) {
            dto.setRefundType("退货退款");
        }
        //售后状态
        dto.setRefundStatus(dealRefundStatus(record.getStatus()));
        //购买时间
        dto.setCreateTime(record.getAfsOrderItem().getCreateTime()
                .format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        //申请时间
        dto.setApplyTime(record.getCreateTime()
                .format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        //审批时间
        Option.of(afsHistoryMap.get(record.getNo())).peek(
                afsHistory -> dto.setApproveTime(afsHistory.getCreateTime()
                        .format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN))));

        //所属店铺
        dto.setShopName(record.getShopName());
        return dto;
    }

    /**
     * 售后状态
     *
     * @param status 售后状态
     * @return 售后状态文案
     */
    private String dealRefundStatus(AfsStatus status) {
        return switch (status) {
            case REFUND_REQUEST -> Constant.REFUND_REQUEST;
            case REFUND_AGREE -> Constant.REFUND_AGREE;
            case SYSTEM_REFUND_AGREE -> Constant.SYSTEM_REFUND_AGREE;
            case REFUND_REJECT -> Constant.REFUND_REJECT;
            case REFUNDED -> Constant.REFUNDED;
            case RETURN_REFUND_REQUEST -> Constant.RETURN_REFUND_REQUEST;
            case RETURN_REFUND_AGREE -> Constant.RETURN_REFUND_AGREE;
            case SYSTEM_RETURN_REFUND_AGREE -> Constant.SYSTEM_RETURN_REFUND_AGREE;
            case RETURN_REFUND_REJECT -> Constant.RETURN_REFUND_REJECT;
            case RETURNED_REFUND -> Constant.RETURNED_REFUND;
            case RETURNED_REFUND_CONFIRM -> Constant.RETURNED_REFUND_CONFIRM;
            case SYSTEM_RETURNED_REFUND_CONFIRM -> Constant.SYSTEM_RETURNED_REFUND_CONFIRM;
            case RETURNED_REFUND_REJECT -> Constant.RETURNED_REFUND_REJECT;
            case RETURNED_REFUNDED -> Constant.RETURNED_REFUNDED;
            case BUYER_CLOSED -> Constant.BUYER_CLOSED;
            case SYSTEM_CLOSED -> Constant.SYSTEM_CLOSED;
            default -> StringUtils.EMPTY;
        };
    }

    /**
     * 价格处理
     *
     * @param price 价格金额
     * @return 价格字符串
     */
    private String dealPrice(Long price) {
        return AmountCalculateHelper.toYuan(price).toPlainString();
    }


    interface Constant {

        String BUYER_CLOSED = "撤销退款";
        String REFUND_REQUEST = "待审核";
        String REFUND_AGREE = "退款中";
        String SYSTEM_REFUND_AGREE = "退款中";
        String REFUND_REJECT = "退款失败";
        String REFUNDED = "退款成功";
        String RETURN_REFUND_REQUEST = "待审核";
        String RETURN_REFUND_AGREE = "待退货";
        String SYSTEM_RETURN_REFUND_AGREE = "待退货";

        String RETURN_REFUND_REJECT = "退款失败";

        String RETURNED_REFUND = "退货中";
        String RETURNED_REFUND_CONFIRM = "退款中";

        String SYSTEM_RETURNED_REFUND_CONFIRM = "退款中";

        String RETURNED_REFUND_REJECT = "退款失败";

        String RETURNED_REFUNDED = "退款成功";

        String SYSTEM_CLOSED = "退款失败";


    }
}
