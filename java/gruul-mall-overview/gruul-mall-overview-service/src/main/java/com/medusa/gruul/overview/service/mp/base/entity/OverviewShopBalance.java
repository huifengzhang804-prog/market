package com.medusa.gruul.overview.service.mp.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * 店铺注册信息
 *
 * @author 张治保
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@TableName("t_overview_shop_balance")
public class OverviewShopBalance extends BaseEntity {


    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商家注册信息id
     */
    private Long shopId;

    /**
     * 总额，不包含未结算余额
     */
    private Long total;

    /**
     * 未提现余额
     */
    private Long undrawn;

    /**
     * 未结算余额
     */
    private Long uncompleted;

    /**
     * 待审核金额
     */
    @TableField(exist = false)
    private Long amountToBeAudited;

    /**
     * 累计提现
     */
    @TableField(exist = false)
    private Long withdrawalTotal;

    /**
     * 提现中
     */
    @TableField(exist = false)
    private Long withdrawing;
}
