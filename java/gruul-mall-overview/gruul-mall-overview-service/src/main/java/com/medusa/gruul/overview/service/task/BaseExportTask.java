package com.medusa.gruul.overview.service.task;

import cn.hutool.core.io.file.FileNameUtil;
import com.google.common.collect.Maps;
import com.medusa.gruul.carrier.pigeon.api.model.dto.UploadParamDTO;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.overview.api.entity.DataExportRecord;
import com.medusa.gruul.overview.api.enums.DataExportStatus;
import com.medusa.gruul.overview.api.enums.OverviewError;
import com.medusa.gruul.overview.service.modules.export.service.IDataExportRecordManageService;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;


/**
 * 文件导出BaseTask接口
 *
 * @author jipeng
 * @date 2024/2/2
 */
public interface BaseExportTask {

    Logger log = LoggerFactory.getLogger(BaseExportTask.class);
    String SHEET_NAME = "SheetJS";


    Integer TEN_THOUSAND = 10000;

    /**
     * 价格处理方法
     *
     * @param price 价格
     * @return 处理之后的价格字符串
     */
    default String dealPrice(Long price) {
        return BigDecimal.valueOf(price).divide(BigDecimal.valueOf(StatementExportTask.TEN_THOUSAND))
                .toString();
    }

    /**
     * 获取上传的RPC服务
     *
     * @return rpcService
     */
    PigeonChatStatisticsRpcService getPigeonChatStatisticsRpcService();

    /**
     * 获取下载模板文件路径
     *
     * @return 模板路径
     */
    String getTaskTempExcelFilePath();

    /**
     * 生成临时Excel文件
     *
     * @param pathName 文件路径
     * @return 生成的临时文件
     */
    default File generateTempExcelFile(String pathName) {
        File generateFile;
        try (InputStream templateInputStream = new ClassPathResource(
                getTaskTempExcelFilePath()).getInputStream()) {
            // 复制资源文件到新的文件
            generateFile = new File(pathName);
            Files.copy(templateInputStream, generateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw OverviewError.TEMP_EXCEL_GENERATE_ERROR.exception();
        }
        return generateFile;
    }

    /**
     * 任务运行
     *
     * @param tempExcelFile 临时文件
     */
    @SneakyThrows
    default void taskRun(File tempExcelFile) {
        DataExportRecord update = new DataExportRecord();
        update.setId(getDataExportDataId());
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
            getDataExportRecordManageService().updateDataExportRecord(update);
        }
    }

    /**
     * 导出EXCEL
     *
     * @param tempExcelFile 临时文件
     * @return 生成记录的条数
     */
    int generateExcel(File tempExcelFile);

    /**
     * 获取下载中心管理控制器类
     *
     * @return service
     */
    IDataExportRecordManageService getDataExportRecordManageService();

    /**
     * 获取数据导出记录id
     *
     * @return 数据导出记录id
     */
    Long getDataExportDataId();

    /**
     * 上传文件到OSS
     *
     * @param tempExcelFile 临时文件
     * @return 文件路径
     */
    default String uploadFileToOSS(File tempExcelFile) {
        try {
            Map<Integer, UploadParamDTO> uploadParamMap = Maps.newHashMapWithExpectedSize(
                    CommonPool.NUMBER_ONE);
            UploadParamDTO uploadParamDTO = new UploadParamDTO();
            try (InputStream inputStream = new FileInputStream(tempExcelFile)) {
                byte[] buffer = new byte[(int) tempExcelFile.length()];
                if (inputStream.read(buffer) != -1) {
                    uploadParamDTO.setFileBytes(buffer);
                    uploadParamDTO.setFormat(FileNameUtil.extName(tempExcelFile));
                } else {
                    throw OverviewError.STATEMENT_UPLOADER_ERROR.exception();
                }
            }
            uploadParamMap.put(CommonPool.NUMBER_ZERO, uploadParamDTO);
            Map<Integer, String> pathMap = getPigeonChatStatisticsRpcService().batchUpload(
                    uploadParamMap);
            return pathMap.get(CommonPool.NUMBER_ZERO);
        } catch (Exception e) {
            log.error("上传失败:", e);
            throw OverviewError.STATEMENT_UPLOADER_ERROR.exception();
        }
    }
}
