package com.medusa.gruul.addon.team.model.vo;

import com.medusa.gruul.addon.team.model.dto.TeamSkuDTO;
import com.medusa.gruul.addon.team.model.enums.TeamMode;
import com.medusa.gruul.addon.team.model.enums.TeamProductStatus;
import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品拼团状态
 *
 * @author 张治保
 * date 2023/3/15
 */

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class TeamProductVO {

    /**
     * 商品拼团状态枚举
     * NO: 不存在拼团活动
     * OPENING: 未开始
     * OPEN: 拼团活动进行中
     */
    private TeamProductStatus teamStatus;


    /**
     * 拼团活动id
     */
    private Long activityId;

    /**
     * 拼团有效时间，单位：分钟，大于等于15
     */
    private Integer effectTimeout;

    /**
     * 拼团模式枚举
     * COMMON: 普通拼团
     * STAIRS: 阶梯拼团
     */
    private TeamMode mode;

    /**
     * 拼团人数列表
     */
    private List<Integer> users;

    /**
     * 是否开启预热模式
     */
    private Boolean preheat;

    /**
     * 预热时间，单位小时，24小时内
     */
    private Short preheatHours;

    /**
     * 是否开启凑团模式
     */
    private Boolean huddle;

    /**
     * 活动开始日期
     */
    private LocalDateTime startTime;

    /**
     * 活动结束日期
     */
    private LocalDateTime endTime;

    /**
     * 叠加优惠配置对象
     */
    private StackableDiscount stackable;

    /**
     * SKU与拼团配置映射对象
     * key: skuId
     * value: TeamProductSkuVO对象
     */
    private List<TeamSkuDTO> skus;

    /**
     * 获取当前选择拼团人数索引
     *
     * @param num 当前选择的拼团人数
     * @return 当前选择拼团人数索引
     */
    public int currentNumIndex(int num) {
        for (int i = 0; i < users.size(); i++) {
            if (num == users.get(i)) {
                return i;
            }
        }
        throw new GlobalException("拼团人数不正确");
    }

    /**
     * 初始化拼团状态
     *
     * @return 拼团状态
     */
    public TeamProductVO initStatus() {
        this.setTeamStatus(
                this.getStartTime().isAfter(LocalDateTime.now()) ? TeamProductStatus.OPENING : TeamProductStatus.OPEN
        );
        return this;
    }
}
