package com.medusa.gruul.addon.distribute.model.vo;

import com.medusa.gruul.addon.distribute.model.enums.DistributeOrderStatus;
import com.medusa.gruul.addon.distribute.model.enums.Level;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 张治保
 * date 2023/5/13
 */
@Getter
@Setter
@ToString
public class DistributorMainOrderVO implements Serializable {

    /**
     * 订单号
     */
    private String orderNo;


    /**
     * 是否属于内购订单
     */
    private Boolean purchase;

    /**
     * 对应的分销商等级
     */
    private Level level;
    /**
     * 当前用户对应的分销商等级
     */
    private Level currentUserLevel;

    /**
     * 买家id
     */
    private Long buyerId;

    /**
     * 买家头像
     */
    private String buyerAvatar;

    /**
     * 买家昵称
     */
    private String buyerNickname;

    /**
     * 买家姓名
     */
    private String buyerName;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 实付款
     */
    private Long payAmount;

    /**
     * 下单时间
     */
    private LocalDateTime createTime;

    /**
     * 订单项
     */
    private List<DistributorOrderVO> items;
    /**
     * 订单状态
     */
    private DistributeOrderStatus status;
}
