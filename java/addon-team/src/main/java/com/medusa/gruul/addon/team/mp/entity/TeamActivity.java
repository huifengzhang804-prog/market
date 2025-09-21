package com.medusa.gruul.addon.team.mp.entity;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.team.model.enums.TeamMode;
import com.medusa.gruul.addon.team.model.enums.TeamStatus;
import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 拼团活动表
 *
 * @author 张治保
 */
@Data
@TableName(value = "t_team_activity", autoResultMap = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TeamActivity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 否否为已违规
     */
//	private Boolean violated;
    /**
     * 活动状态
     */
    private TeamStatus status;

    /**
     * 违规下架原因
     */
    @TableField("violation_reason")
    private String violationReason;


    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 拼团活动名称
     */
    private String name;

    /**
     * 活动开始日期
     */
    private LocalDateTime startTime;

    /**
     * 活动结束日期
     */
    private LocalDateTime endTime;

    /**
     * 拼团有效时间，单位：分钟，大于等于15
     */
    private Integer effectTimeout;

    /**
     * 拼团模式 [COMMON,STAIRS]
     */
    private TeamMode mode;

    /**
     * 参团人数
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<Integer> users;

    /**
     * 是否开启模拟成团
     */
    private Boolean simulate;

    /**
     * 是否开启凑团模式
     */
    private Boolean huddle;

    /**
     * 是否可叠加优惠（会员，优惠券，满减）
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private StackableDiscount stackable;


    /**
     * 活动开启时间 开启了预热 以预热活动开始时间为准 未开启预热 以活动开始时间-5分钟为准
     */
    public LocalDateTime openTime() {
        LocalDateTime openTime = getStartTime();

        return openTime.minusMinutes(CommonPool.NUMBER_FIVE);
    }

    /**
     * 与time比较 是否可开启
     *
     * @param time 例如当前时间
     */
    public Boolean canOpen(LocalDateTime time) {
        return LocalDateTimeUtil.between(time, openTime()).toMinutes() < CommonPool.NUMBER_THREE;
    }

    /**
     * 是否未开始
     *
     * @return 未开始 true 其他false
     */
    public Boolean notStarted() {

        if (TeamStatus.VIOLATION.equals(status) || TeamStatus.SHOP_OFF_SHELF.equals(status)) {
            return Boolean.FALSE;
        }
        //未开始
        if (LocalDateTime.now().isBefore(startTime)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 是否进行中
     *
     * @return 进行中 true 其他false
     */
    public Boolean processing() {

        if (TeamStatus.VIOLATION.equals(status) || TeamStatus.SHOP_OFF_SHELF.equals(status)) {
            return Boolean.FALSE;
        }
        //进行中
        if (LocalDateTime.now().isAfter(startTime) && LocalDateTime.now().isBefore(endTime)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 是否已结束
     *
     * @return 已结束 true 其他false
     */
    public Boolean finished() {

        if (TeamStatus.VIOLATION.equals(status) || TeamStatus.SHOP_OFF_SHELF.equals(status)) {
            return Boolean.FALSE;
        }
        //已结束
        if (LocalDateTime.now().isAfter(endTime)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 是否违规下架
     *
     * @return 违规下架 true 其他 false
     */
    public Boolean illegalSellOff() {

        return TeamStatus.VIOLATION.equals(status);
    }

    /**
     * 是否下架
     *
     * @return 下架 true 其他 false
     */
    public Boolean shopSellOff() {

        return TeamStatus.SHOP_OFF_SHELF.equals(status);
    }


    /**
     * 是否存在
     *
     * @return 存在true 不存在false
     */
//    public Boolean exists() {
//        ClientType clientType = ISystem.clientTypeOpt().getOrNull();
//        //平台端访问 且平台端的删除
//        if (ClientType.PLATFORM_CONSOLE.equals(clientType) && platformDeleteFlag) {
//            return Boolean.FALSE;
//        }
//        //非平台端访问 且店铺端的删除标记
//        if (!ClientType.PLATFORM_CONSOLE.equals(clientType) && shopDeleteFlag) {
//            return Boolean.FALSE;
//        }
//
//        return Boolean.TRUE;
//    }
}
