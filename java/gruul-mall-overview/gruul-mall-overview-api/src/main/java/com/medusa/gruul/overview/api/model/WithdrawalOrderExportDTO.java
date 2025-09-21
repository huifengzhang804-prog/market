package com.medusa.gruul.overview.api.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @description: 提现工单导出DTO
 * @projectName:gruul-mall-overview
 * @see:com.medusa.gruul.overview.api.model
 * @author:jipeng
 * @createTime:2024/1/23 18:22
 * @version:1.0
 */
@Data
public class WithdrawalOrderExportDTO {

  //  4、根据模板样式导出：
//  编号、
  @ExcelProperty(value = "编号")
  private String no;
  //  申请编号、
  @ExcelProperty(value = "申请编号")
  private String applyNo;
  //  用户名称、
  @ExcelProperty(value = "用户名称")
  private String userName;
  //  手机号、
  @ExcelProperty(value = "手机号")
  private String mobile;
  //  提现金额、
  @ExcelProperty(value = "提现金额")
  private String amount;
  //  提现方式
  @ExcelProperty(value = "提现方式")
  private String withdrawTo;
  //  提现类型、
  @ExcelProperty(value = "提现类型")
  private String userType;


  //  状态、
  @ExcelProperty(value = "状态")
  private String status;
  /**
   * 打款方式 线上线下
   */
  @ExcelProperty(value = "打款方式")
  private String paymentWay;
  @ExcelProperty(value = "打款时间")
  private String paymentTime;
  /**
   * 失败原因
   */
  @ExcelProperty(value = "失败原因")
  private String failReason;
  //  拒绝原因
  @ExcelProperty(value = "拒绝原因")
  private String rejectReason;
  //  申请时间、
  @ExcelProperty(value = "申请时间")
  private String applyTime;

  @ExcelProperty(value = "审批员")
  private String auditUserName;
  @ExcelProperty(value = "审批员手机号")
  private String auditUserPhone;
  //  审批时间
  @ExcelProperty(value = "审批时间")
  private String auditTime;
  @ExcelProperty(value = "备注")
  private String remark;



}
