package com.medusa.gruul.overview.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.overview.api.enums.DataExportStatus;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 数据导出记录表
 *
 * @author jipeng
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@TableName("t_data_export_record")
public class DataExportRecord extends BaseEntity {

  /**
   * 店铺id
   */
  @TableField(value = "shop_id")
  private Long shopId;

  /**
   * 导出用户id
   */
  @TableField(value = "export_user_id")
  private Long exportUserId;
  /**
   * 导出用户名称
   */
  @TableField(value = "user_phone")
  private String userPhone;
  /**
   * 导出数据类型
   */
  @TableField(value = "data_type")
  private ExportDataType dataType;
  /**
   * 导出状态
   */
  @TableField(value = "status")
  private DataExportStatus status;
  /**
   * 导出数据数量
   */
  @TableField(value = "data_count")
  private Integer dataCount;
  /**
   * 导出文件大小
   */
  @TableField(value = "file_size")
  private Integer fileSize;
  /**
   * 导出文件名称
   */
  @TableField(value = "file_name")
  private String fileName;
  /**
   * 导出文件路径
   */
  @TableField(value = "file_path")
  private String filePath;






}
