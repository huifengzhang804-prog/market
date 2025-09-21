package com.medusa.gruul.overview.api.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.handler.type.MapToKeyValuesTypeHandler;
import com.medusa.gruul.overview.api.enums.DrawType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * 分销商提现账户
 *
 * @author 张治保
 * date 2023/5/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "t_overview_withdraw_accounts", autoResultMap = true)
public class OverviewWithdrawAccounts implements Serializable {

    /**
     * 用户id
     */
    @TableId(type = IdType.INPUT)
    private Long userId;

    /**
     * 提现账户详情 key:提现方式 value:账户详情
     */
    @TableField(typeHandler = MapToKeyValuesTypeHandler.class)
    private Map<DrawType, JSONObject> accounts;
}
