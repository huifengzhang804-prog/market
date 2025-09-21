package com.medusa.gruul.addon.distribute.service.task;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Maps;
import com.medusa.gruul.addon.distribute.model.dto.DistributorOrderQueryDTO;
import com.medusa.gruul.addon.distribute.model.enums.DistributeError;
import com.medusa.gruul.addon.distribute.model.enums.DistributeOrderStatus;
import com.medusa.gruul.addon.distribute.service.DistributeOrderHandleService;
import com.medusa.gruul.carrier.pigeon.api.model.dto.UploadParamDTO;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.overview.api.enums.DataExportStatus;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;

/**
 * @author jipeng
 * @since 2025/2/17
 */
@Data
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractDistributeOrderExportTask implements Runnable {

    public static final Integer TEN_THOUSAND = 10000;
    public static final String SHEET_NAME = "SheetJS";

    private final DistributeOrderHandleService distributeOrderHandleService;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    private final DataExportRecordRpcService dataExportRecordRpcService;
    private final UserRpcService userRpcService;
    private final OrderRpcService orderRpcService;
    private final DistributorOrderQueryDTO queryDTO;
    private final DataExportRecordDTO dataExportRecordDTO;

    /**
     * 运行导出数据的任务
     *
     * @param tempExcelFile
     */

    @SneakyThrows
    protected void taskRun(File tempExcelFile) {
        DataExportRecordDTO update = new DataExportRecordDTO();
        update.setId(getDataExportRecordDTO().getId());
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
     * @param tempExcelFile
     * @return
     */
    abstract Integer generateExcel(File tempExcelFile);


    protected File generateTempExcelFile(String distributeOrderFilePath) {
        String tempPath = "/分销订单_%s.xlsx";
        String basePath = String.format(tempPath, IdUtil.fastUUID());
        File generateFile;
        try (InputStream templateInputStream = new ClassPathResource(distributeOrderFilePath).getInputStream()) {
            // 复制资源文件到新的文件
            generateFile = new File(basePath);
            Files.copy(templateInputStream, generateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            throw DistributeError.TEMP_EXCEL_GENERATE_ERROR.exception();
        }
        return generateFile;
    }

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
                    throw DistributeError.DISTRIBUTE_ORDER_UPLOADER_ERROR.exception();
                }
            }
            uploadParamMap.put(CommonPool.NUMBER_ZERO, uploadParamDTO);
            Map<Integer, String> pathMap = pigeonChatStatisticsRpcService.batchUpload(uploadParamMap);
            return pathMap.get(CommonPool.NUMBER_ZERO);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw DistributeError.DISTRIBUTE_ORDER_UPLOADER_ERROR.exception();
        }
    }

    protected String dealItemStatus(DistributeOrderStatus orderStatus) {
        return switch (orderStatus) {
            case UNPAID -> "待付款";
            case PAID -> "待发货";
            case COMPLETED -> "已完成";
            case CLOSED -> "已关闭";
        };
    }

    protected String dealPrice(Long price) {
        return BigDecimal.valueOf(price)
                .divide(BigDecimal.valueOf(TEN_THOUSAND), CommonPool.NUMBER_TWO, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString();
    }
}
