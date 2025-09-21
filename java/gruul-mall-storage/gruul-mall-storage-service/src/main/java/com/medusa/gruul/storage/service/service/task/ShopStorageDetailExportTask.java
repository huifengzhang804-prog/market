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
import com.medusa.gruul.storage.service.service.task.dto.ShopStorageDetailExportDTO;
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
 * 店铺库存明细导出任务
 *
 * @author jipeng
 * @since 2025/3/17
 */
public class ShopStorageDetailExportTask extends AbstractStorageDetailExportTask {

    public static final String SHOP_STORAGE_DETAIL_FILE_PATH = "/temp/商家端--库存明细.xlsx";

    public ShopStorageDetailExportTask(PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService,
                                       DataExportRecordRpcService dataExportRecordRpcService,
                                       UserRpcService userRpcService,
                                       QueryStorageDetailService queryStorageDetailService,
                                       StorageDetailParam queryDTO,
                                       Long recorderId) {
        super(pigeonChatStatisticsRpcService, dataExportRecordRpcService, userRpcService,
                queryStorageDetailService, queryDTO, recorderId);
    }

    @Override
    public void run() {
        File tempExcelFile = generateTempExcelFile(SHOP_STORAGE_DETAIL_FILE_PATH);
        taskRun(tempExcelFile);
    }


    @Override
    public List<ShopStorageDetailExportDTO> data(List<StorageDetail> records, AtomicInteger counter) {
        List<ShopStorageDetailExportDTO> result = Lists.newArrayList();
        for (StorageDetail record : records) {
            ShopStorageDetailExportDTO dto = new ShopStorageDetailExportDTO();
            dto.setIndex(String.valueOf(counter.getAndIncrement()));
            dto.setBillNo(String.valueOf(record.getId()));
            dto.setSpuId(String.valueOf(record.getProductId()));
            dto.setSpuName(record.getProductName());
            dto.setSpec(Objects.nonNull(record.getSpecName()) ? Joiner.on(",").join(record.getSpecName()) : StringUtils.EMPTY);
            dto.setSkuId(String.valueOf(record.getSkuId()));
            if (StockType.LIMITED.equals(record.getSkuStockType())) {
                dto.setStockChangeNum(String.valueOf(record.getStockChangeNum()));
            } else {
                dto.setStockChangeNum("无限库存");
            }

            dto.setStockChangeType(record.getStockChangeType().getDesc());
            if (Objects.nonNull(record.getProduct())) {
                dto.setSpuType(record.getProduct().getProductType().getDesc());
                dto.setSpuSource(record.getProduct().getSellType().getDesc());
            }
            dto.setOrderNo(record.getOrderNo());
            dto.setStockChangeTime(record.getCreateTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            result.add(dto);
        }
        return result;
    }

    @Override
    ExcelWriter fetchExcelWriter(File excelFile) throws IOException {
        return EasyExcel.write(excelFile, ShopStorageDetailExportDTO.class)
//                    .registerWriteHandler(new CellStyleStrategy())
                .withTemplate(new ClassPathResource(SHOP_STORAGE_DETAIL_FILE_PATH).getInputStream())
                .needHead(Boolean.FALSE)
                .build();

    }
}
