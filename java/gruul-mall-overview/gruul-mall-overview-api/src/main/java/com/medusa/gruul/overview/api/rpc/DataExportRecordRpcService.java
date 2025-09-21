package com.medusa.gruul.overview.api.rpc;

import com.medusa.gruul.overview.api.model.DataExportRecordDTO;

/**
 * 数据导出RPC接口
 *
 * @author jipeng
 * @since 2024/3/15
 */
public interface DataExportRecordRpcService {

  /**
   * 保存导出记录数据
   * @param dataExportRecordDTO 导出数据记录
   * @return 导出数据id
   */
  Long saveExportRecord(DataExportRecordDTO dataExportRecordDTO);

  /**
   * 更新 导出记录数据
   * @param dataExportRecordDTO 数据记录信息
   */
  void updateExportRecordData(DataExportRecordDTO dataExportRecordDTO);
}
