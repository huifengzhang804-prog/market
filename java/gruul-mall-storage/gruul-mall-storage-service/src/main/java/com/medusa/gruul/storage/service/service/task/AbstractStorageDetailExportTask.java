package com.medusa.gruul.storage.service.service.task;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import com.medusa.gruul.carrier.pigeon.api.model.dto.UploadParamDTO;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.overview.api.enums.DataExportStatus;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.storage.service.model.enums.StorageError;
import com.medusa.gruul.storage.service.model.param.StorageDetailParam;
import com.medusa.gruul.storage.service.mp.entity.StorageDetail;
import com.medusa.gruul.storage.service.service.QueryStorageDetailService;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 库存明细导出任务
 *
 * @author jipeng
 * @since 2025/3/17
 */
@Data
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractStorageDetailExportTask implements Runnable {
    public static final Integer TEN_THOUSAND = 10000;
    public static final String SHEET_NAME = "SheetJS";


    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    private final DataExportRecordRpcService dataExportRecordRpcService;
    private final UserRpcService userRpcService;
    private final QueryStorageDetailService queryStorageDetailService;

    private final StorageDetailParam queryDTO;
    private final Long recorderId;

    @SneakyThrows
    protected void taskRun(File tempExcelFile) {
        DataExportRecordDTO update = new DataExportRecordDTO();
        update.setId(getRecorderId());
        try {
            int count = generateExcel(tempExcelFile);
            log.debug("订单导出成功,数据条数为:{}", count);
            //上传文件到OSS
            String path = uploadFileToOSS(tempExcelFile);
            update.setFilePath(path);
            update.setDataCount(count);
            update.setFileSize(Long.valueOf(tempExcelFile != null ? tempExcelFile.length() : 0).intValue());
            update.setStatus(DataExportStatus.SUCCESS);
        } catch (Exception e) {
            log.error("订单导出失败", e);
            //导出失败
            update.setStatus(DataExportStatus.FAILED);
        } finally {
            if (Objects.nonNull(tempExcelFile)) {
                FileUtils.delete(tempExcelFile);
            }
            getDataExportRecordRpcService().updateExportRecordData(update);
        }
    }

    /**
     * 生成EXCEL文件
     *
     * @param
     * @return
     */

    Integer generateExcel(File excelFile) {
        AtomicInteger counter = new AtomicInteger(CommonPool.NUMBER_ONE);
        ExcelWriter excelWriter = null;
        try {
            excelWriter = fetchExcelWriter(excelFile);
            WriteSheet writeSheet = EasyExcel.writerSheet(SHEET_NAME).build();
            while (true) {
                IPage<StorageDetail> page = getQueryStorageDetailService().getStorageDetailList(
                        getQueryDTO());
                List<StorageDetail> records = page.getRecords();
                if (records.isEmpty()) {
                    break;
                }
                getQueryDTO().setCurrent(getQueryDTO().getCurrent() + CommonPool.NUMBER_ONE);
                excelWriter.write(data(records, counter), writeSheet);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(excelWriter)) {
                excelWriter.close();
            }
        }

        return counter.get() - 1;

    }

    /**
     * 获取EXCELWriter
     *
     * @param excelFile
     * @return
     * @throws IOException
     */
    abstract ExcelWriter fetchExcelWriter(File excelFile) throws IOException;

    /**
     * 生成Excel数据
     *
     * @param records
     * @param counter
     * @return
     * @throws IOException
     */
    abstract List<? extends Object> data(List<StorageDetail> records, AtomicInteger counter) throws IOException;

    protected String uploadFileToOSS(File tempExcelFile) {
        try {
            Map<Integer, UploadParamDTO> uploadParamMap = Maps.newHashMapWithExpectedSize(CommonPool.NUMBER_ONE);
            UploadParamDTO uploadParamDTO = new UploadParamDTO();
            try (InputStream inputStream = new FileInputStream(tempExcelFile)) {
                byte[] buffer = new byte[(int) tempExcelFile.length()];
                if (inputStream.read(buffer) != -1) {
                    uploadParamDTO.setFileBytes(buffer);
                    uploadParamDTO.setFormat(FileNameUtil.extName(tempExcelFile));
                } else {
                    throw StorageError.EXCEL_EXPORT_ERROR.exception();
                }
            }
            uploadParamMap.put(CommonPool.NUMBER_ZERO, uploadParamDTO);
            Map<Integer, String> pathMap = pigeonChatStatisticsRpcService.batchUpload(uploadParamMap);
            return pathMap.get(CommonPool.NUMBER_ZERO);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw StorageError.EXCEL_EXPORT_ERROR.exception();
        }
    }

    protected File generateTempExcelFile(String distributeOrderFilePath) {
        String tempPath = "/库存明细_%s.xlsx";
        String basePath = String.format(tempPath, IdUtil.fastUUID());
        File generateFile;
        try (InputStream templateInputStream = new ClassPathResource(distributeOrderFilePath).getInputStream()) {
            // 复制资源文件到新的文件
            generateFile = new File(basePath);
            Files.copy(templateInputStream, generateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw StorageError.TEMP_EXCEL_GENERATE_ERROR.exception();
        }
        return generateFile;
    }

}
