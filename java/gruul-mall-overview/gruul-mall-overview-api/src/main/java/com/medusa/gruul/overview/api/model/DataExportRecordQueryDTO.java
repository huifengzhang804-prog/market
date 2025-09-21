package com.medusa.gruul.overview.api.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.overview.api.entity.DataExportRecord;
import com.medusa.gruul.overview.api.enums.DataExportStatus;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @description:
 * @projectName:gruul-mall-overview
 * @see:com.medusa.gruul.overview.api.model
 * @author:jipeng
 * @createTime:2024/1/6 11:38
 * @version:1.0
 */
@Data
@Accessors(chain = true)
public class DataExportRecordQueryDTO extends Page<DataExportRecord> {
  /**
   * 导出数据类型
   */
  private ExportDataType dataType;
  /**
   * 导出状态
   */
  private DataExportStatus status;
  /**
   * 手机号
   */
  private String userPhone;
  /**
   * 导出开始时间
   */
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date exportStartDate;
  /**
   * 导出结束时间
   */
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date exportEndDate;

}
