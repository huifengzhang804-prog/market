package com.medusa.gruul.overview.service.modules.export.service.rpc;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.overview.api.entity.DataExportRecord;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.overview.service.modules.export.service.IDataExportRecordManageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 数据导出对外接口Service实现类
 *
 * @author jipeng
 * @since 2024/3/15
 */
@Service
@DubboService
@Slf4j
public class DataExportRecordRpcServiceImpl implements DataExportRecordRpcService {

    @Resource
    private IDataExportRecordManageService dataExportRecordManageService;

    @Override
    public Long saveExportRecord(DataExportRecordDTO dto) {

        return dataExportRecordManageService.saveDataExportRecord(dto.getExportUserId(),
                dto.getShopId(), dto.getUserPhone(), dto.getDataType());
    }

    @Override
    @Log("更新导出记录")
    public void updateExportRecordData(DataExportRecordDTO dataExportRecordDTO) {
        DataExportRecord dataExportRecord = BeanUtil.copyProperties(dataExportRecordDTO,
                DataExportRecord.class);
        dataExportRecordManageService.updateDataExportRecord(dataExportRecord);
    }
}
