package com.medusa.gruul.overview.service.modules.export.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.overview.api.entity.DataExportRecord;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.model.DataExportRecordQueryDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 下载中心Service
 *
 * @author jipeng
 */
public interface IDataExportRecordManageService {

    /**
     * 保存数据导出记录
     *
     * @param userId   用户id
     * @param shopId   店铺id
     * @param phone    手机号
     * @param dataType 数据类型
     * @return 数据导出记录ID
     */
    Long saveDataExportRecord(Long userId, Long shopId, String phone, ExportDataType dataType);

    /**
     * 更新数据导出记录
     *
     * @param dataExportRecord 更新导出记录的DTO
     */
    void updateDataExportRecord(DataExportRecord dataExportRecord);

    /**
     * 批量删除
     *
     * @param ids 批量删除的ids
     */
    Boolean batchRemove(List<Long> ids);

    /**
     * 单条记录删除
     *
     * @param id 要删除的记录id
     */
    void removeById(Long id);

    /**
     * 文件下载
     *
     * @param id       要下载的记录id
     * @param response HTTP输出流
     */
    void downloadDataExportRecord(Long id, HttpServletResponse response);

    /**
     * 分页数据查询
     *
     * @param dataExportRecordQueryDTO 查询条件
     * @return 导出记录列表
     */
    Page<DataExportRecordDTO> list(DataExportRecordQueryDTO dataExportRecordQueryDTO);

    /**
     * 查询正在生成中的数量
     *
     * @return 正在生成中的数量
     */
    Long generateCount();
}
