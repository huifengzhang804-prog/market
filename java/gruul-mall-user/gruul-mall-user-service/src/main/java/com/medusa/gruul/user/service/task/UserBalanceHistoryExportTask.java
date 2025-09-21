package com.medusa.gruul.user.service.task;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.medusa.gruul.carrier.pigeon.api.model.dto.UploadParamDTO;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.overview.api.enums.DataExportStatus;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.user.api.enums.UserError;
import com.medusa.gruul.user.service.model.dto.UserBalanceHistoryDTO;
import com.medusa.gruul.user.service.model.dto.UserBalanceHistoryExportDTO;
import com.medusa.gruul.user.service.model.vo.UserBalanceHistoryQueryVo;
import com.medusa.gruul.user.service.service.IUserBalanceHistoryManageService;

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

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

/**
 * 用户储值流水导出任务
 *
 * @author jipeng
 * date 2024/2/2
 */
@RequiredArgsConstructor
@Slf4j
public class UserBalanceHistoryExportTask implements Runnable {

    public static final String TEMP_EXCEL_PATH = "temp/储值流水.xlsx";
    public static final Integer TEN_THOUSAND = 10000;
    private static final String SHEET_NAME = "SheetJS";
    private final Long exportRecord;
    private final DataExportRecordRpcService dataExportRecordRpcService;

    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;

    private final IUserBalanceHistoryManageService userBalanceHistoryManageService;

    private final UserBalanceHistoryQueryVo userBalanceHistoryQueryVo;

    private static File generateTempExcel() {
        File generateFile;
        try (InputStream templateInputStream = new ClassPathResource(
                TEMP_EXCEL_PATH).getInputStream()) {
            // 复制资源文件到新的文件
            generateFile = new File("/储值流水_" + IdUtil.fastUUID() + ".xlsx");
            Files.copy(templateInputStream, generateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw UserError.TEMP_EXCEL_GENERATE_ERROR.exception();
        }
        return generateFile;
    }

    public static String dealPrice(Long price) {
        return BigDecimal.valueOf(price).divide(BigDecimal.valueOf(TEN_THOUSAND), CommonPool.NUMBER_TWO, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    @Override
    @SneakyThrows
    public void run() {
        File tempExcelFile = generateTempExcel();
        DataExportRecordDTO update = new DataExportRecordDTO();
        update.setId(exportRecord);
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
        try (ExcelWriter excelWriter = EasyExcel.
                write(tempExcelFile, UserBalanceHistoryExportDTO.class)
                .withTemplate(new ClassPathResource(TEMP_EXCEL_PATH).getInputStream())
                .needHead(Boolean.FALSE)
                .build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet(SHEET_NAME).build();
            while (true) {
                Page<UserBalanceHistoryDTO> page = userBalanceHistoryManageService.list(
                        userBalanceHistoryQueryVo);
                List<UserBalanceHistoryDTO> records = page.getRecords();
                if (records.size() == CommonPool.NUMBER_ZERO) {
                    break;
                }
                userBalanceHistoryQueryVo.setCurrent(userBalanceHistoryQueryVo.getCurrent() + CommonPool.NUMBER_ONE);
                excelWriter.write(data(records, counter), writeSheet);
            }

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
        return counter.get() - CommonPool.NUMBER_ONE;

    }

    private List<UserBalanceHistoryExportDTO> data(List<UserBalanceHistoryDTO> records, AtomicInteger counter) {
        List<UserBalanceHistoryExportDTO> result = Lists.newArrayListWithCapacity(records.size());
        for (UserBalanceHistoryDTO record : records) {
            UserBalanceHistoryExportDTO dto = new UserBalanceHistoryExportDTO();
            dto.setIndex(String.valueOf(counter.getAndIncrement()));
            dto.setNo(String.valueOf(record.getNo()));
            dto.setUserNickName(record.getUserNickName());
            dto.setUserPhone(record.getUserPhone());
            dto.setOperatorTypeStr(record.getOperatorTypeStr());
            dto.setAmountStr(record.getAmountStr());
            dto.setAfterAmountStr(String.valueOf(record.getAfterAmount()));
            dto.setOrderNo(record.getOrderNo());
            dto.setOperatorUserNickName(record.getOperatorUserNickName());
            dto.setOperatorTime(record.getCreateTime().
                    format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            dto.setRemark(record.getRemark());
            result.add(dto);
        }
        return result;
    }

    private String uploadFileToOSS(File tempExcelFile) {
        try {
            Map<Integer, UploadParamDTO> uploadParamMap = Maps.newHashMapWithExpectedSize(
                    CommonPool.NUMBER_ONE);
            UploadParamDTO uploadParamDTO = new UploadParamDTO();
            try (InputStream inputStream = new FileInputStream(tempExcelFile)) {
                //后续文件长度过长的话，不要一次性读取
                byte[] buffer = new byte[(int) tempExcelFile.length()];
                if (inputStream.read(buffer) != -1) {
                    uploadParamDTO.setFileBytes(buffer);
                    uploadParamDTO.setFormat(FileNameUtil.extName(tempExcelFile));
                } else {
                    throw UserError.EXCEL_UPLOADER_ERROR.exception();
                }
            }
            uploadParamMap.put(CommonPool.NUMBER_ZERO, uploadParamDTO);
            Map<Integer, String> pathMap = pigeonChatStatisticsRpcService.batchUpload(
                    uploadParamMap);
            return pathMap.get(CommonPool.NUMBER_ZERO);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw UserError.EXCEL_UPLOADER_ERROR.exception();
        }
    }

}
