package com.medusa.gruul.user.service.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @description:会员购买记录导出DTO
 * @projectName:gruul-mall-user
 * @see:com.medusa.gruul.user.service.model.dto
 * @author:jipeng
 * @createTime:2024/1/17 18:01
 * @version:1.0
 */
@Data
public class MemberPurchaseHistoryExportDTO {

//3、根据模板样式导出：
    /**
     * 编号
     */
    @ExcelProperty(value = "编号")
    private String index;
    /**
     * 订单号
     */
    @ExcelProperty(value = "订单号")
    private String orderNo;
    /**
     * 用户昵称
     */
    @ExcelProperty(value = "用户昵称")
    private String nickName;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号")
    private String userPhone;

    /**
     * 会员等级
     */
    @ExcelProperty(value = "会员等级")
    private String level;
    /**
     * 有效期
     */
    @ExcelProperty(value = "有效期")
    private String effectiveDuration;
    /**
     * 支付金额
     */
    @ExcelProperty(value = "支付金额")
    private String payAmount;

    /**
     * 类型
     */
    @ExcelProperty(value = "类型")
    private String type;
    /**
     * 购买时间
     */
    @ExcelProperty(value = "购买时间")
    private String buyTime;
    /**
     * 到期时间
     */
    @ExcelProperty(value = "到期时间")
    private String expireTime;

}
