package com.medusa.gruul.addon.bargain.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.bargain.model.enums.ActivityStatus;
import com.medusa.gruul.addon.bargain.model.enums.HelpCutAmountType;
import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 砍价活动表
 *
 * @author WuDi
 * @since 2023-03-14
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_bargain", autoResultMap = true)
public class Bargain extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 砍价活动名称
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
     * 砍价人数
     */
    private Integer bargainingPeople;

    /**
     * 砍价有效期
     */
    private Integer bargainValidityPeriod;

    /**
     * 是否自我砍价
     */
    private Boolean isSelfBargain;

//	/**
//	 * 用户类型
//	 */
//	private UserType userType;

//	/**
//	 * 活动预热时间
//	 */
//	private Integer activityPreheat;

    /**
     * 是否可叠加优惠（会员，优惠券，满减）
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private StackableDiscount stackable;

    /**
     * 活动状态
     */
    private ActivityStatus status;

    /**
     * 违规下架原因
     */
    @TableField("violation_reason")
    private String violationReason;

    /**
     * 砍价类型
     */
    private HelpCutAmountType helpCutAmount;

    /**
     * 活动商品数
     */
    private Integer productNum;

    /**
     * 参与人数
     */
    private Integer peopleNum;

    /**
     * 支付订单数
     */
    private Integer payOrder;

    /**
     * 应收金额
     */
    private Long amountReceivable;

//	/**
//	 * 平台删除标记
//	 */
//	@TableField("platform_delete_flag")
//	private Boolean platformDeleteFlag = Boolean.FALSE;
//	/**
//	 * 店铺删除标记
//	 */
//	@TableField("shop_delete_flag")
//	private Boolean shopDeleteFlag = Boolean.FALSE;


    /**
     * 发起人砍价结束时间
     */
    @TableField(exist = false)
    private LocalDateTime sponsorBargainEndTime;

//	/**
//	 * 是否存在
//	 * @return 是否存在 true 存在 false 不存在
//	 *
//	 */
//	public boolean exists(){
//		//通过访问的平台来进行判断
//		ClientType clientType = ISystem.clientTypeOpt().getOrNull();
//		if (ClientType.PLATFORM_CONSOLE.equals(clientType)&&platformDeleteFlag) {
//			//如果是平台端访问的 判断 平台端删除的标记位
//			return Boolean.FALSE;
//		}
//		//非平台端访问的 判断 shopDeleteFlag
//		if (shopDeleteFlag){
//			return Boolean.FALSE;
//		}
//		return Boolean.TRUE;
//	}

    /**
     * 是否违规下架
     *
     * @return 违规下架 true 其他 false
     */
    public boolean illegalSellOff() {
        return ActivityStatus.ILLEGAL_SELL_OFF.equals(status);
    }

    /**
     * 是否店铺下架
     *
     * @return 店铺下架 true  其他false
     */
    public boolean shopSellOff() {
        return ActivityStatus.SHOP_SELL_OFF.equals(status);
    }

    /**
     * 活动是否未开始
     *
     * @return未开始 true  其他false
     */
    public boolean notStarted() {
        if (ActivityStatus.SHOP_SELL_OFF.equals(status) ||
                ActivityStatus.ILLEGAL_SELL_OFF.equals(status)) {
            return Boolean.FALSE;
        }
        if (LocalDateTime.now().isBefore(startTime)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 活动是否进行中
     *
     * @return
     */
    public boolean processing() {
        if (ActivityStatus.SHOP_SELL_OFF.equals(status) ||
                ActivityStatus.ILLEGAL_SELL_OFF.equals(status)) {
            return Boolean.FALSE;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(startTime) && now.isBefore(endTime)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 活动是否结束
     *
     * @return 结束 true  其他false
     */
    public boolean finished() {
        if (ActivityStatus.SHOP_SELL_OFF.equals(status) ||
                ActivityStatus.ILLEGAL_SELL_OFF.equals(status)) {
            return Boolean.FALSE;
        }
        if (LocalDateTime.now().isAfter(endTime)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
}
