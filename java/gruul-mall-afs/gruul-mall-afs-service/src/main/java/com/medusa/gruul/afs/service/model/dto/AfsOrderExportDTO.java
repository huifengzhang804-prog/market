package com.medusa.gruul.afs.service.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @description: 售后工单导出DTO
 * @projectName:gruul-mall-afs
 * @see:com.medusa.gruul.afs.api.model
 * @author:jipeng
 * @createTime:2024/1/12 13:33
 * @version:1.0
 */
@Data
public class AfsOrderExportDTO {

  //  编号、
  @ExcelProperty("编号")
  private String index;
  //  订单编号、
  @ExcelProperty("订单编号")
  private String orderNo;

  //  买家昵称、
  @ExcelProperty("买家昵称")
  private String buyerNickname;
  //  买家手机号、
  @ExcelProperty("买家手机号")
  private String buyerMobile;
  /**
   * 商品编号
   */
  @ExcelProperty("商品编号(SPUID)")
  private String productId;
  //  退款商品名
  @ExcelProperty("退款商品名")
  private String productName;

  //规格编号
  @ExcelProperty("规格编号(SKUID)")
  private String skuId;
  //  、规格、
  @ExcelProperty("规格")
  private String productSpec;


  @ExcelProperty("成交价")
  private String dealPrice;

  //  退款数、
  @ExcelProperty("退款数")
  private String refundCount;
  //  退款金额、
  @ExcelProperty("退款金额")
  private String refundAmount;
  //  退款类型、
  @ExcelProperty("退款类型")
  private String refundType;
  // 售后状态
  @ExcelProperty("售后状态")
  private String refundStatus;
  //  购买时间、
  @ExcelProperty("购买时间")
  private String createTime;
  //  申请时间、
  @ExcelProperty("申请时间")
  private String applyTime;
  //  审批时间、
  @ExcelProperty("审批时间")
  private String approveTime;
  //  所属店铺
  @ExcelProperty("所属店铺")
  private String shopName;

}
