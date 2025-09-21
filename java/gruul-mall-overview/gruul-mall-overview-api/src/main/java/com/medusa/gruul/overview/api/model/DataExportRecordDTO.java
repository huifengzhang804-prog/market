package com.medusa.gruul.overview.api.model;

import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.overview.api.enums.DataExportStatus;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 导出记录DTO
 */
@Data
@Accessors(chain = true)
public class DataExportRecordDTO implements BaseDTO {

  /**
   * 导出记录id
   */
  private Long id;

  private Long shopId;
  /**
   * 导出状态
   */
  private DataExportStatus status;
  /**
   * 数据类型
   */
  private ExportDataType dataType;

  private String exportDataTypeStr;
  /**
   * 导出用户id
   */

  private Long exportUserId;
  /**
   * 导出用户手机
   */
  private String userPhone;
  /**
   * 导出数据条数
   */

  private Integer dataCount;
  /*
   * 导出文件大小
   */
  private Integer fileSize;
  /**
   * 导出文件名称
   */
  private String filePath;

  private LocalDateTime createTime;

  public static DataExportRecordDTO instanceForSave(Long userId,Long shopId,
          ExportDataType exportDataType,String userPhone){
      if (Objects.isNull(userId)||
              Objects.isNull(shopId)||
              Objects.isNull(exportDataType)||
              Objects.isNull(userPhone)) {
          throw new GlobalException(SystemCode.PARAM_MISS);
      }
     DataExportRecordDTO dataExportRecordDTO=new DataExportRecordDTO();
     dataExportRecordDTO.setExportUserId(userId)
            .setShopId(shopId)
            .setDataType(exportDataType)
            .setUserPhone(userPhone)
            .setStatus(DataExportStatus.PROCESSING);
     return dataExportRecordDTO;

  }


}
