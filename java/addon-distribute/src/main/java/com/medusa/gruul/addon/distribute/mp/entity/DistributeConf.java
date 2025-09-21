package com.medusa.gruul.addon.distribute.mp.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.distribute.model.DistributorCondition;
import com.medusa.gruul.addon.distribute.model.enums.Level;
import com.medusa.gruul.addon.distribute.model.enums.Precompute;
import com.medusa.gruul.addon.distribute.model.enums.ShareType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 分销配置
 *
 * @author 张治保
 * @since 2022-11-14
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_distribute_conf", autoResultMap = true)
public class DistributeConf implements Serializable {


    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 分销层级 1一级 2二级 3三级
     */
    @TableField("`level`")
    private Level level;

    /**
     * 成为分销员的条件类型
     */
    @TableField(value = "`condition`", typeHandler = Fastjson2TypeHandler.class)
    private DistributorCondition condition;

    /**
     * 预计算展示方式
     */
    private Precompute precompute;

    /**
     * 分销协议
     */
    private String protocol;

    /**
     * 攻略玩法
     */
    private String playMethods;

    /**
     * 是否开启内购
     */
    private Boolean purchase;

    /**
     * 佣金类型 佣金类型 1.统一设置 2.固定金额 3.百分比
     */
    private ShareType shareType;

    /**
     * 一级分佣
     */
    private Long one;

    /**
     * 二级分佣
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long two;

    /**
     * 三级分佣
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long three;


}
