package com.medusa.gruul.order.service.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.BooleanEnum;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import lombok.Data;

/**
 * @description:订单导出的数据
 * @projectName:gruul-mall-order
 * @see:com.medusa.gruul.order.service.model.dto
 * @author:jipeng
 * @createTime:2024/1/8 11:24
 * @version:1.0
 */
@Data
@ColumnWidth(20)
@HeadFontStyle(fontHeightInPoints = 12, fontName = "宋体", bold = BooleanEnum.FALSE)
@HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 40) // 自定义RGB颜色 // 设置表头背景色
public class OrderExportDTO {

    @ExcelProperty("编号")
    private String no;

    @ExcelProperty("订单编号")
    private String orderNo;

    @ExcelProperty("订单状态")
    private String orderStatus;

    @ExcelProperty("买家昵称")
    private String buyerNickName;

    @ExcelProperty("买家手机号")
    private String buyerPhone;

    @ExcelProperty("商品编号(SPUID)")
    private String productId;
    /**
     * 商品名称
     */
    @ExcelProperty("商品名称")
    private String productName;

    /**
     * 商品来源
     */
    @ExcelProperty("商品来源")
    private String productSource;

    /**
     * 商品规格
     */
    @ExcelProperty("商品规格")
    private String productSpec;
    /**
     * 规格编号
     */
    @ExcelProperty("规格编号(SKUID)")
    private String skuId;

    /**
     * 销售价
     */
    @ExcelProperty("销售价")
    private String productSellPrice;

    /**
     * 购买数量
     */
    @ExcelProperty("购买数量")
    private String productNum;

    /**
     * 已发货数
     */
    @ExcelProperty("已发货数")
    private String productSendNum;

    /**
     * 待发货数
     */
    @ExcelProperty("待发货数")
    private String productWaitSendNum;

    /**
     * 店铺优惠
     */
    @ExcelProperty("店铺优惠")
    private String productShopDiscount;

    /**
     * 平台优惠
     */
    @ExcelProperty("平台优惠")
    private String productPlatformDiscount;

    /**
     * 会员折扣
     */
    @ExcelProperty("会员折扣")
    private String productMemberDiscount;

    /**
     * 消费返利
     */
    @ExcelProperty("消费返利")
    private String productConsumeRefund;

    /**
     * 商品总价
     */
    @ExcelProperty("商品总价")
    private String productTotalPrice;

    /**
     * 运费
     */
    @ExcelProperty("运费")
    private String productFreightPrice;

    /**
     * 平台总优惠
     */
    @ExcelProperty("平台总优惠")
    private String productPlatformTotalDiscount;

    /**
     * 店铺总优惠
     */
    @ExcelProperty("店铺总优惠")
    private String productShopTotalDiscount;

    /**
     * 应付款
     */
    @ExcelProperty("应付款")
    private String productPayPrice;

    /**
     * 支付状态
     */
    @ExcelProperty("支付状态")
    private String productPayStatus;

    /**
     * 支付方式
     */
    @ExcelProperty("支付方式")
    private String productPayType;

    /**
     * 配送方式
     */
    @ExcelProperty("配送方式")
    private String productDeliveryType;

    /**
     * 所属渠道
     */
    @ExcelProperty("所属渠道")
    private String productChannel;

    /**
     * 收货人姓名
     */
    @ExcelProperty("收货人姓名")
    private String productReceiverName;

    /**
     * 收货人电话
     */
    @ExcelProperty("收货人电话")
    private String productReceiverPhone;

    /**
     * 收货地址
     */
    @ExcelProperty("收货地址")
    private String productReceiverAddress;

    /**
     * 所属店铺
     */
    @ExcelProperty("所属店铺")
    private String productShopName;

    /**
     * 支付时间
     */
    @ExcelProperty("支付时间")
    private String productPayTime;

    /**
     * 下单时间
     */
    @ExcelProperty("下单时间")
    private String productCreateTime;

    /**
     * 用户备注
     */
    @ExcelProperty("用户备注")
    private String productRemark;

}
