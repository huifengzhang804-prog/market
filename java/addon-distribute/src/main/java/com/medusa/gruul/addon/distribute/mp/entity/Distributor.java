package com.medusa.gruul.addon.distribute.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.distribute.model.enums.DistributorIdentity;
import com.medusa.gruul.addon.distribute.model.enums.DistributorStatus;
import com.medusa.gruul.addon.distribute.model.enums.Level;
import com.medusa.gruul.addon.distribute.model.vo.DistributorStatistics;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 分销商
 *
 * @author 张治保
 * @since 2022-11-16
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_distributor")
public class Distributor extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 成为分销商后的专属分销码
     */
    @TableField("`code`")
    private String code;

    /**
     * 身份 1.分销员 2.分销商
     */
    private DistributorIdentity identity;

    /**
     * 总佣金
     */
    private Long total;

    /**
     * 未提现佣金
     */
    private Long undrawn;

    /**
     * 待结算佣金
     */
    private Long unsettled;

    /**
     * 已失效佣金
     */
    private Long invalid;

    /**
     * 申请成为分销商的申请状态
     */
    @TableField("`status`")
    private DistributorStatus status;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 真实姓名
     */
    @TableField("`name`")
    private String name;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 一级分销商的用户id  /分页查询作为一级分销员统计
     */
    private Long one;

    /**
     * 二级分销商的用户id /分页查询作为二级分销员统计
     */
    private Long two;

    /**
     * 三级分销商的的用户id /分页查询作为三级分销员统计
     */
    private Long three;

    /**
     * 申请成为分销商的时间
     */
    private LocalDateTime applyTime;

    /**
     * 审核员
     */
    private String auditor;

    /**
     * 成为分销商的时间(申请通过时间)
     * 字段有限 不通过的时间也是这个
     */
    private LocalDateTime passTime;
    /**
     * 是否已访问
     */
    @TableField("visited")
    private Boolean visited;
    /**
     * 审核不通过原因
     */
    @TableField("`reject_reason`")
    private String rejectReason;


    /**
     * 推荐人
     */
    @TableField(exist = false)
    private String referrer;

    /**
     * 平台分销配置
     */
    @TableField(exist = false)
    private DistributeConf config;

    /**
     * 个人统计
     */
    @TableField(exist = false)
    private DistributorStatistics statistics;


    /**
     * 当前分销员属于分销商的第几级
     */
    @TableField(exist = false)
    private Level level;

    /**
     * 个人消费金额
     */
    @TableField(exist = false)
    private Long consumption;

    /**
     * 个人订单统计
     */
    @TableField(exist = false)
    private Integer orderCount;
}
