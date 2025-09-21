package com.medusa.gruul.overview.api.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @description: 平台对账单导出DTO
 * @projectName:gruul-mall-overview
 * @see:com.medusa.gruul.overview.api.model
 * @author:jipeng
 * @createTime:2024/1/13 14:43
 * @version:1.0
 */
@Data
public class PlateStatementExportDTO {

  //  3、根据模板样式导出：
//  编号、
  @ExcelProperty(value = "编号")
  private String index;
  //  交易流水号、
  @ExcelProperty(value = "交易流水号")
  private String tradeNo;
  //  订单号、
  @ExcelProperty(value = "订单号")
  private String orderNo;
  //  交易类型、
  @ExcelProperty(value = "交易类型")
  private String tradeType;
  //  收支金额、
  @ExcelProperty(value = "收支金额")
  private String amount;
  //  所属商家、
  @ExcelProperty(value = "所属商家")
  private String shopName;
  //  交易时间
  @ExcelProperty(value = "交易时间")
  private String tradeTime;
}
