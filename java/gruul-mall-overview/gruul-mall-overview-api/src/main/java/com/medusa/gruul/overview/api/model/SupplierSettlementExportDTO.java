package com.medusa.gruul.overview.api.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @description: 供应商结算导出DTO
 * @projectName:gruul-mall-overview
 * @see:com.medusa.gruul.overview.api.model
 * @author:jipeng
 * @createTime:2024/1/16 10:51
 * @version:1.0
 */
@Data
public class SupplierSettlementExportDTO {

  //2、根据模板样式导出：店铺余额、提现中、累计提现、编号、交易流水号、金额、状态、申请时间、审批时间、拒绝说明
  //编号
  @ExcelProperty(value = "编号")
  private String index;
  //交易流水号
  @ExcelProperty(value = "交易流水号")
  private String tradeNo;
  //金额
  @ExcelProperty(value = "金额")
  private String amount;
  //状态
  @ExcelProperty(value = "状态")
  private String status;
  /**
   * 申请人
   */
  @ExcelProperty(value = "申请人")
  private String applyUserNickName;

  @ExcelProperty(value = "手机号")
  private String applyUserPhone;
  //申请时间
  @ExcelProperty(value = "申请时间")
  private String applyTime;
  //审批时间
  @ExcelProperty(value = "审批时间")
  private String approveTime;
  //拒绝说明
  @ExcelProperty(value = "拒绝说明")
  private String rejectReason;


}
