package com.medusa.gruul.payment.service.mp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.github.binarywang.wxpay.bean.applyment.ApplymentStateQueryResult;
import com.github.binarywang.wxpay.bean.applyment.enums.ApplymentStateEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.EnumTypeHandler;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 特约商户申请表
 *
 * @author 张治保
 * date 2023/6/5
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "t_merchant_sub", autoResultMap = true)
public class MerchantSub implements Serializable {
    /**
     * 店铺id
     */
    @TableId(type = IdType.INPUT)
    private Long shopId;

    /**
     * 业务申请编号
     */
    private String businessCode;

    /**
     * 特约商户商户号 审核通过是会取得改数据
     */
    private String subMchid;

    /**
     * 申请单id
     */
    private String applymentId;

    /**
     * 申请单状态同步
     */
    @TableField(typeHandler = EnumTypeHandler.class)
    private ApplymentStateEnum applymentState;

    /**
     * 申请单状态同步结果
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private ApplymentStateQueryResult queryResp;

    /**
     * 是否启用 一个店铺只能启用一个
     */
    private Boolean confirmed;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

