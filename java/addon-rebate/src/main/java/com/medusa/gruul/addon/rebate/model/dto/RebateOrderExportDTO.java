package com.medusa.gruul.addon.rebate.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**消费返利订单导出DTO
 * @author jipeng
 * @since 2025/2/22
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
public class RebateOrderExportDTO {
//    编号	订单编号	订单状态	买家昵称	买家手机号	商品编号(SPUID)	商品名称	商品来源	规格
//    规格编号(SKUID)	销售价	购买数量	店铺优惠	平台优惠	会员折扣	消费返利	商品总价	运费
//    平台总优惠	店铺总优惠	应付款	平台服务费	预计返利	结算状态	支付状态	支付方式	所属店铺	支付时间	下单时间

    @ExcelProperty(value = "编号")
    private String no;

    @ExcelProperty(value = "订单编号")
    private String orderNo;

    @ExcelProperty(value = "订单状态")
    private String orderStatus;

    @ExcelProperty(value = "买家昵称")
    private String buyerNickName;

    @ExcelProperty(value = "买家手机号")
    private String buyerPhone;

    @ExcelProperty(value = "商品编号(SPUID)")
    private String spuId;

    @ExcelProperty(value = "商品名称")
    private String spuName;

    @ExcelProperty(value = "商品来源")
    private String spuSource;

    @ExcelProperty(value = "规格")
    private String spec;

    @ExcelProperty(value = "规格编号(SKUID)")
    private String skuId;

    @ExcelProperty(value = "销售价")
    private String salePrice;

    @ExcelProperty(value = "购买数量")
    private String buyNum;

    @ExcelProperty(value = "店铺优惠")
    private String storeDiscount;

    @ExcelProperty(value = "平台优惠")
    private String platformDiscount;

    @ExcelProperty(value = "会员折扣")
    private String memberDiscount;

    @ExcelProperty(value = "消费返利")
    private String rebate;

    @ExcelProperty(value = "商品总价")
    private String totalPrice;

    @ExcelProperty(value = "运费")
    private String freightPrice;

    @ExcelProperty(value = "平台总优惠")
    private String platformTotalDiscount;

    @ExcelProperty(value = "店铺总优惠")
    private String storeTotalDiscount;

    @ExcelProperty(value = "应付款")
    private String payable;

    @ExcelProperty(value = "平台服务费")
    private String platformServiceCharge;

    @ExcelProperty(value = "预计返利")
    private String expectRebate;

    @ExcelProperty(value = "结算状态")
    private String settlementStatus;

    @ExcelProperty(value = "支付状态")
    private String payStatus;

    @ExcelProperty(value = "支付方式")
    private String payType;

    @ExcelProperty(value = "所属店铺")
    private String shopName;

    @ExcelProperty(value = "支付时间")
    private String payTime;

    @ExcelProperty(value = "下单时间")
    private String orderTime;


}
