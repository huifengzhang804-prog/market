package com.medusa.gruul.order.api.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.vavr.control.Option;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/9/3
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("t_order_timeout")
public class OrderTimeout implements Serializable {

    //支付超时时间 默认15分钟
    public static final long DEFAULT_PAY_TIME_OUT = 15 * 60;
    //确认收货超时时间 默认15天
    public static final long DEFAULT_CONFIRM_TIME_OUT = 15 * 24 * 60 * 60;
    //评价超时时间 默认15天
    public static final long DEFAULT_COMMENT_TIME_OUT = DEFAULT_CONFIRM_TIME_OUT;
    //售后审核超时时间 默认3天
    public static final long DEFAULT_AFS_AUDIT_TIME_OUT = 30 * 24 * 60 * 60;
    /**
     * 支付超时时间 单位/秒
     * 最少15分钟 最长30天
     */
    @NotNull
    @Min(15 * 60)
    @Max(30 * 24 * 60 * 60)
    private Long payTimeout;
    /**
     * 确认收获超时时间 单位 秒
     * 最少3天 最长30天
     */
    @NotNull
    @Min(3 * 24 * 60 * 60)
    @Max(30 * 24 * 60 * 60)
    private Long confirmTimeout;
    /**
     * 评价超时时间 单位秒
     * 最少3天 最长30天
     */
    @NotNull
    @Min(3 * 24 * 60 * 60)
    @Max(30 * 24 * 60 * 60)
    private Long commentTimeout;
    /**
     * 售后审核超时时间 单位秒
     * 最少1天 最长30天
     */
    @NotNull
    @Min(24 * 60 * 60)
    @Max(30 * 24 * 60 * 60)
    private Long afsAuditTimeout;

    /**
     * 获取支付超时时间 毫秒
     *
     * @return 支付超时时间 毫秒
     */
    @JSONField(serialize = false)
    public Long getPayTimeoutMills() {
        return Option.of(getPayTimeout())
                .getOrElse(DEFAULT_PAY_TIME_OUT) * 1000;
    }

    /**
     * 获取自动确认收货超时时间 毫秒
     *
     * @return 自动确认收货超时时间 毫秒
     */
    @JSONField(serialize = false)
    public Long getConfirmTimeoutMills() {
        return Option.of(getConfirmTimeout())
                .getOrElse(DEFAULT_CONFIRM_TIME_OUT) * 1000;
    }


    /**
     * 获取自动好评超时时间 毫秒
     *
     * @return 自动好评超时时间 毫秒
     */
    @JSONField(serialize = false)
    public Long getCommentTimeoutMills() {
        return Option.of(getCommentTimeout())
                .getOrElse(DEFAULT_COMMENT_TIME_OUT) * 1000;
    }

}
