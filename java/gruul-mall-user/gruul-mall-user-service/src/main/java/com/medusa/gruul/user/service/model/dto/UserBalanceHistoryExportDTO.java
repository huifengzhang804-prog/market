package com.medusa.gruul.user.service.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @description:
 * @projectName:gruul-mall-user
 * @see:com.medusa.gruul.user.service.model.dto
 * @author:jipeng
 * @createTime:2024/1/20 15:34
 * @version:1.0
 */
@Data
public class UserBalanceHistoryExportDTO {
    /**
     * 编号
     */
    @ExcelProperty(value = "编号")
    private String index;

    /**
     * 流水编号
     */
    @ExcelProperty(value = "流水编号")
    private String no;

    /**
     * 用户昵称
     */
    @ExcelProperty(value = "用户昵称")
    private String userNickName;
    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号")
    private String userPhone;
    /**
     * 操作类型
     */
    @ExcelProperty(value = "操作类型")
    private String operatorTypeStr;

    /**
     * 变动金额
     */
    @ExcelProperty(value = "变动金额")
    private String amountStr;
    /**
     * 期后金额
     */
    @ExcelProperty(value = "期后金额")
    private String afterAmountStr;
    /**
     * 关联订单
     */
    @ExcelProperty(value = "关联订单")
    private String orderNo;
    /**
     * 操作人
     */
    @ExcelProperty(value = "操作人")
    private String operatorUserNickName;
    /**
     * 操作时间
     */
    @ExcelProperty(value = "操作时间")
    private String operatorTime;
    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

}
