package com.medusa.gruul.addon.rebate.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.rebate.model.enums.RebateType;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 返利明细表
 * </p>
 *
 * @author WuDi
 * @since 2023-07-17
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_rebate_details")
public class RebateDetails extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 返利类型
     */
    private RebateType rebateType;

    /**
     * 返利名称
     */
    private String rebateName;


    /**
     * 用户id
     */
    private Long userId;

    /**
     * 金额
     */
    private Long amount;

    /**
     * 收入支出类型
     */
    private ChangeType changeType;


}
