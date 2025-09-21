package com.medusa.gruul.addon.rebate.mp.entity;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.rebate.model.enums.RebateUsers;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 消费返利设置表
 * </p>
 *
 * @author WuDi
 * @since 2023-07-17
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_rebate_conf", autoResultMap = true)
public class RebateConf extends BaseEntity {


    /**
     * 消费返利状态
     */
    private Boolean rebateStatus;

    /**
     * 返利用户
     */
    private RebateUsers rebateUsers;

    /**
     * 付费返利用户json
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<RebateUsersExtendValue> payRebateUsers;

    /**
     * 所有返利用户json
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<RebateUsersExtendValue> allRebateUsers;

    /**
     * 获取用户返利用户配置
     *
     * @return 用户返利用户配置
     */
    public List<RebateUsersExtendValue> vipRebateConfigs() {
        return this.rebateUsers == RebateUsers.PAID_MEMBER ? this.payRebateUsers : this.allRebateUsers;
    }

    /**
     * 返利是否是禁用状态
     */
    public boolean disabled() {
        return !BooleanUtil.isTrue(getRebateStatus());
    }


}
