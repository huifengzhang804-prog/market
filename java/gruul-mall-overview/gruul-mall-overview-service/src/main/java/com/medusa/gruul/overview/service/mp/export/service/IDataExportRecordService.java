package com.medusa.gruul.overview.service.mp.export.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.overview.api.entity.DataExportRecord;
import com.medusa.gruul.overview.api.model.DataExportRecordQueryDTO;

import java.util.List;

/**
 * 数据导出记录
 *
 * @author jipeng
 * @date 2024/2/2
 */
public interface IDataExportRecordService extends IService<DataExportRecord> {
  /**
   * 删除下载记录
   * @param id 记录id
   * @param shopRole 角色
   * @return 成功 true  失败 false
   */
  Boolean removeByIdAndRole(Long id, Boolean shopRole);
  /**
   * 批量删除下载记录
   * @param ids 记录ids
   * @param shopRole 角色
   * @return 成功 true  失败 false
   */
  Boolean remove(List<Long> ids, Boolean shopRole);
  /**
   * 获取导出记录文件
   * @param id 记录id
   * @param shopRole 角色
   * @return
   */
  DataExportRecord downloadDataExportRecord(Long id, Boolean shopRole);
  /**
   * 导出记录列表查询
   * @param dataExportRecordQueryDTO 查询条件
   * @param shopRole 角色
   * @return
   */
  IPage<DataExportRecord> queryList(DataExportRecordQueryDTO dataExportRecordQueryDTO, Boolean shopRole);
}
