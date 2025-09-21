package com.medusa.gruul.overview.api.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.enums.TransactionType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.global.model.enums.ReceiverType;
import com.medusa.gruul.overview.api.model.TradeDetailModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * <p>
 * 对账单
 * </p>
 *
 * @author WuDi
 * @since 2022-10-09
 */
@Getter
@Setter
@ToString(callSuper = true)
@TableName(value = "t_overview_statement", autoResultMap = true)
@Accessors(chain = true)
public class OverviewStatement extends BaseEntity {


    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息id 幂等性校验id 可用于做分账单号
     */
    private String msgId;

    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 交易类型
     */
    private TransactionType tradeType;


    /**
     * 交易流水号
     */
    private String tradeNo;

    /**
     * 交易时间
     */
    private LocalDateTime tradeTime;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 交易详情
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private TradeDetailModel tradeDetail;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 资金流向:0->收入; 1->支出
     */
    private ChangeType changeType;

    /**
     * 收支金额
     */
    private Long amount;

    /**
     * 店铺名称
     */
    private String shopName;

    @TableField(value = "target_shop_id")
    private Long targetShopId;

    /**
     * 是否是分账账单
     */
    private Boolean shared;

    /**
     * 分账对象
     */
    private ReceiverType sharingTarget;

    /**
     * 额外数据
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private JSONObject extra = new JSONObject();
}
