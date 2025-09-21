package com.medusa.gruul.addon.supplier.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @description: 采购订单导出DTO
 * @projectName:addon-supplier
 * @see:com.medusa.gruul.addon.supplier.model.dto
 * @author:jipeng
 * @createTime:2024/1/11 9:21
 * @version:1.0
 */
@Data
public class PurchaseOrderExportDTO {

  //  编号、
  @ExcelProperty("编号")
  private String index;

  //  订单编号、
  @ExcelProperty("订单编号")
  private String orderNo;
  //  订单状态、
  @ExcelProperty("订单状态")
  private String status;
  //商品编号
  @ExcelProperty("商品编号")
  private String productId;
  //  商品名称、
  @ExcelProperty("商品名称")
  private String productName;
  //  商品来源、
//  @ExcelProperty("商品来源")
//  private String productSource;
  /**
   * 商品类型
   */
  @ExcelProperty("商品类型")
  private String productType;

  @ExcelProperty("规格编号(SKUID)")
  private String sukId;
  //  规格、
  @ExcelProperty("规格")
  private String productSpec;
  //  采购单价、
  @ExcelProperty("采购单价")
  private String productPrice;
  //  采购数、
  @ExcelProperty("采购数")
  private String productNum;
  //  已发货数、
  @ExcelProperty("已发货数")
  private String deliveryNum;
  //  待发货数、
  @ExcelProperty("待发货数")
  private String waitDeliveryNum;
  //  已入库数、
  @ExcelProperty("已入库数")
  private String storageNum;
  //  剩余入库数、
  @ExcelProperty("剩余入库数")
  private String waitStorageNum;
  //  商品总价、
  @ExcelProperty("商品总价")
  private String productTotalPrice;
  //  运费、
  @ExcelProperty("运费")
  private String freight;
  //  应付款、
  @ExcelProperty("应付款")
  private String payment;
  //  支付状态、
  @ExcelProperty("支付状态")
  private String paymentStatus;
  //  支付方式、
  @ExcelProperty("支付方式")
  private String paymentType;
  //  供应商、
  @ExcelProperty("供应商")
  private String supplier;
  //  供应商手机号、
  @ExcelProperty("供应商手机号")
  private String supplierPhone;
  //  买家昵称、
  @ExcelProperty("采购员")
  private String buyerNickName;
  //  买家手机号、
  @ExcelProperty("手机号")
  private String buyerPhone;
  //  收货人姓名、
  @ExcelProperty("收货人姓名")
  private String consigneeName;
  //  收货人电话、
  @ExcelProperty("收货人电话")
  private String consigneePhone;
  //  收货地址、
  @ExcelProperty("收货地址")
  private String consigneeAddress;
  //  支付时间、
  @ExcelProperty("支付时间")
  private String paymentTime;
  //  下单时间、
  @ExcelProperty("下单时间")
  private String createTime;
  //  所属店铺
  @ExcelProperty("所属店铺")
  private String shopName;
  //联系电话
  @ExcelProperty("联系电话")
  private String concatPhone;
}
