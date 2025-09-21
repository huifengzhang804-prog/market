package com.medusa.gruul.storage.service.service.task;

import cn.hutool.core.date.DatePattern;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.storage.api.enums.StockType;
import com.medusa.gruul.storage.service.model.param.StorageDetailParam;
import com.medusa.gruul.storage.service.mp.entity.StorageDetail;
import com.medusa.gruul.storage.service.service.QueryStorageDetailService;
import com.medusa.gruul.storage.service.service.task.dto.SupplierStorageDetailExportDO;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jipeng
 * @since 2025/3/18
 */
public class SupplierStorageDetailExportTask extends AbstractStorageDetailExportTask {

    public static final String SUPPLIER_STORAGE_DETAIL_FILE_PATH = "/temp/供应商端--库存明细.xlsx";

    public SupplierStorageDetailExportTask(PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService,
                                           DataExportRecordRpcService dataExportRecordRpcService,
                                           UserRpcService userRpcService,
                                           QueryStorageDetailService queryStorageDetailService,
                                           StorageDetailParam queryDTO,
                                           Long recorderId) {
        super(pigeonChatStatisticsRpcService, dataExportRecordRpcService,
                userRpcService, queryStorageDetailService,
                queryDTO, recorderId);
    }


    @Override
    ExcelWriter fetchExcelWriter(File excelFile) throws IOException {
        return EasyExcel.write(excelFile, SupplierStorageDetailExportDO.class)
//                    .registerWriteHandler(new CellStyleStrategy())
                .withTemplate(new ClassPathResource(SUPPLIER_STORAGE_DETAIL_FILE_PATH).getInputStream())
                .needHead(Boolean.FALSE)
                .build();
    }

    @Override
    List<? extends Object> data(List<StorageDetail> records, AtomicInteger counter) throws IOException {
        List<SupplierStorageDetailExportDO> result = Lists.newArrayList();
        for (StorageDetail record : records) {
            SupplierStorageDetailExportDO dto = new SupplierStorageDetailExportDO();
            dto.setIndex(String.valueOf(counter.getAndIncrement()));
            dto.setBillNo(String.valueOf(record.getId()));
            dto.setSpuId(String.valueOf(record.getProductId()));

            dto.setSpec(Objects.nonNull(record.getSpecName()) ? Joiner.on(",").join(record.getSpecName()) : StringUtils.EMPTY);
            dto.setSkuId(String.valueOf(record.getSkuId()));
            if (StockType.LIMITED.equals(record.getSkuStockType())) {
                dto.setStockChangeNum(String.valueOf(record.getStockChangeNum()));
            } else {
                dto.setStockChangeNum("无限库存");
            }
            dto.setStockChangeType(record.getStockChangeType().getDesc());
            if (Objects.nonNull(record.getProduct())) {
                dto.setSpuName(record.getProduct().getName());
                dto.setSpuType(record.getProduct().getProductType().getDesc());
                dto.setSellType(record.getProduct().getSellType().getDesc());
            }
            dto.setOrderNo(record.getOrderNo());
            dto.setStockChangeTime(record.getCreateTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            result.add(dto);
        }
        return result;
    }

    @Override
    public void run() {
        File tempExcelFile = generateTempExcelFile(SUPPLIER_STORAGE_DETAIL_FILE_PATH);
        taskRun(tempExcelFile);
    }
}
