package com.medusa.gruul.addon.coupon.task;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.medusa.gruul.addon.coupon.model.dto.CouponUseRecordExportDTO;
import com.medusa.gruul.addon.coupon.model.dto.CouponUserDTO;
import com.medusa.gruul.addon.coupon.model.enums.CouponErrorEnum;
import com.medusa.gruul.addon.coupon.model.vo.CouponUserVO;
import com.medusa.gruul.addon.coupon.service.ConsumerCouponService;
import com.medusa.gruul.carrier.pigeon.api.model.dto.UploadParamDTO;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.overview.api.enums.DataExportStatus;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用券记录导出任务
 */
@RequiredArgsConstructor
@Slf4j
public class CouponUserExportTask implements Runnable {

    /**
     * 模板文件路径
     */
    private static final String TEMP_FILE_PATH = "temp/用券记录_temp.xlsx";

    /**
     * SHEET名称
     */
    private static final String SHEET_NAME = "SheetJS";

    /**
     * 导出记录id
     */
    private final Long exportRecordId;


    private final CouponUserDTO couponUserDTO;
    private final ConsumerCouponService consumerCouponService;
    private final DataExportRecordRpcService dataExportRecordRpcService;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;

    private static File generateTempExcel() {
        File generateFile;
        try (InputStream templateInputStream = new ClassPathResource(TEMP_FILE_PATH).getInputStream()) {
            // 复制资源文件到新的文件
            generateFile = new File("/用券记录_" + IdUtil.fastUUID() + ".xlsx");
            Files.copy(templateInputStream, generateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error(CouponErrorEnum.TEMP_EXCEL_GENERATE_ERROR.getMsgCode(), e);
            throw CouponErrorEnum.TEMP_EXCEL_GENERATE_ERROR.exception();
        }
        return generateFile;
    }

    @SneakyThrows
    @Override
    public void run() {

        File tempExcelFile = generateTempExcel();
        DataExportRecordDTO update = new DataExportRecordDTO();
        update.setId(exportRecordId);
        try {
            int count = generateExcel(tempExcelFile);

            log.debug("导出成功,数据条数为:{}", count);
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
                FileUtils.delete(tempExcelFile);
            }
            dataExportRecordRpcService.updateExportRecordData(update);
        }
    }

    /**
     * 生成临时EXCEL
     *
     * @param tempExcelFile 临时文件
     * @return 总数据条数
     */
    private int generateExcel(File tempExcelFile) {
        AtomicInteger counter = new AtomicInteger(CommonPool.NUMBER_ONE);
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel
                    .write(tempExcelFile, CouponUseRecordExportDTO.class)
                    .withTemplate(new ClassPathResource(TEMP_FILE_PATH).getInputStream())
                    .needHead(Boolean.FALSE)
                    .build();
            WriteSheet writeSheet = EasyExcel.writerSheet(SHEET_NAME).build();
            while (true) {
                // 获取需要导出的数据
                IPage<CouponUserVO> page = consumerCouponService.useRecord(couponUserDTO);
                List<CouponUserVO> records = page.getRecords();
                if (records.isEmpty()) {
                    break;
                }
                // 页码加1, 按页查询数据后写入文件
                couponUserDTO.setCurrent(couponUserDTO.getCurrent() + CommonPool.NUMBER_ONE);
                // 写入数据
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

    /**
     * 数据处理
     */
    private List<CouponUseRecordExportDTO> data(List<CouponUserVO> records, AtomicInteger counter) {
        List<CouponUseRecordExportDTO> list = Lists.newArrayListWithCapacity(records.size());

        for (CouponUserVO record : records) {
            CouponUseRecordExportDTO exportDTO = new CouponUseRecordExportDTO();
            exportDTO.setIndex(String.valueOf(counter.getAndIncrement()));
            exportDTO.setCouponUserId(String.valueOf(record.getCouponUserId()));
            exportDTO.setUserNickname(record.getUserNickname());
            exportDTO.setUserPhone(record.getUserPhone());
            exportDTO.setName(record.getName());
            // exportDTO.setParValue(parValue(record.getAmount(), record.getDiscount()));
            exportDTO.setParValue(record.getParValue());
            exportDTO.setQueryStatus(record.getQueryStatusText());
            exportDTO.setType(record.getType().getText());
            exportDTO.setCouponTypeDescription(record.getTypeDescription());
            exportDTO.setCollectType(record.getCollectTypeText());
            exportDTO.setAssociatedOrderNo(record.getAssociatedOrderNo());
            exportDTO.setCreateTime(record.getCreateTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            exportDTO.setEndDate(record.getEndDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
            exportDTO.setGiftUserPhone(record.getGiftUserPhone());
            list.add(exportDTO);
        }

        return list;
    }


    /**
     * 文件上传
     */
    private String uploadFileToOSS(File tempExcelFile) {
        try {
            Map<Integer, UploadParamDTO> uploadParamMap = Maps.newHashMapWithExpectedSize(CommonPool.NUMBER_ONE);
            UploadParamDTO uploadParamDTO = new UploadParamDTO();
            try (InputStream inputStream = new FileInputStream(tempExcelFile)) {
                byte[] buffer = new byte[(int) tempExcelFile.length()];
                if (inputStream.read(buffer) != -1) {
                    uploadParamDTO.setFileBytes(buffer);
                    uploadParamDTO.setFormat(FileNameUtil.extName(tempExcelFile));
                } else {
                    throw CouponErrorEnum.EXCEL_UPLOADER_ERROR.exception();
                }
            }
            uploadParamMap.put(CommonPool.NUMBER_ZERO, uploadParamDTO);
            Map<Integer, String> pathMap = pigeonChatStatisticsRpcService.batchUpload(uploadParamMap);
            return pathMap.get(CommonPool.NUMBER_ZERO);
        } catch (Exception e) {
            log.error(CouponErrorEnum.EXCEL_UPLOADER_ERROR.getMsgCode(), e);
            throw CouponErrorEnum.EXCEL_UPLOADER_ERROR.exception();
        }
    }


    /**
     * 优惠金额
     * 折扣比
     */
    private String parValue(Long amount, BigDecimal discount) {
        if (null != amount) {
            return BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(CommonPool.UNIT_CONVERSION_TEN_THOUSAND), CommonPool.NUMBER_TWO, RoundingMode.HALF_UP).toPlainString();
        } else if (null != discount) {
            return discount.stripTrailingZeros().toPlainString() + "折";
        }
        return null;
    }

}
