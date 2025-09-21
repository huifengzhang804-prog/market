package com.medusa.gruul.addon.distribute.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.medusa.gruul.addon.distribute.model.enums.DistributorStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 分销商申请状态 vo
 *
 * @author 张治保
 * @since 2024/9/12
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DistributorApplyStatusVO {

    /**
     * 申请状态
     */
    private DistributorStatus status;

    /**
     * 分销码
     */
    private String distributeCode;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 审核时间  通过/拒绝时显示这个字段
     */
    private LocalDateTime auditTime;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 是否已访问
     */
    @TableField("visited")
    private Boolean visited;

}
