package com.medusa.gruul.addon.distribute.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 平台分销订单导出DTO
 *
 * @author jipeng
 * @since 2025/2/14
 */
@Data
public class PlatformDistributeOrderExportDTO {
//    编号	订单编号	订单状态	买家昵称	买家手机号	商品编号（SPUID）	商品名称	规格	规格编号（SKUID）	销售价
//    购买数量	应付款	所属店铺	是否内购	内购佣金	一级佣金	二级佣金	三级佣金	结算状态
//    一级分销员昵称	一级分销员手机号	二级分销员昵称	二级分销员手机号	三级分销员昵称
//    三级分销员手机号	支付状态	支付时间	下单时间

    @ExcelProperty(value = "编号")
    private String index;
    @ExcelProperty(value = "订单编号")
    private String no;
    @ExcelProperty(value = "订单状态")
    private String status;
    @ExcelProperty(value = "买家昵称")
    private String buyerNickName;
    @ExcelProperty(value = "买家手机号")
    private String buyerPhone;
    @ExcelProperty(value = "商品编号（SPUID）")
    private String spuId;
    @ExcelProperty(value = "商品名称")
    private String spuName;
    @ExcelProperty(value = "规格")
    private String spec;
    @ExcelProperty(value = "规格编号（SKUID）")
    private String skuId;
    @ExcelProperty(value = "销售价")
    private String salePrice;
    @ExcelProperty(value = "购买数量")
    private String buyNum;
    @ExcelProperty(value = "应付款")
    private String payable;
    @ExcelProperty(value = "所属店铺")
    private String shopName;
    @ExcelProperty(value = "是否内购")
    private String isInner;
    @ExcelProperty(value = "内购佣金")
    private String innerCommission;
    @ExcelProperty(value = "一级佣金")
    private String firstCommission;
    @ExcelProperty(value = "二级佣金")
    private String secondCommission;
    @ExcelProperty(value = "三级佣金")
    private String thirdCommission;
    @ExcelProperty(value = "结算状态")
    private String settlementStatus;
    @ExcelProperty(value = "一级分销员昵称")
    private String firstDistributeNickName;
    @ExcelProperty(value = "一级分销员手机号")
    private String firstDistributePhone;
    @ExcelProperty(value = "二级分销员昵称")
    private String secondDistributeNickName;
    @ExcelProperty(value = "二级分销员手机号")
    private String secondDistributePhone;
    @ExcelProperty(value = "三级分销员昵称")
    private String thirdDistributeNickName;
    @ExcelProperty(value = "三级分销员手机号")
    private String thirdDistributePhone;
    @ExcelProperty(value = "支付状态")
    private String payStatus;
    @ExcelProperty(value = "支付时间")
    private String payTime;
    @ExcelProperty(value = "下单时间")
    private String orderTime;


}
