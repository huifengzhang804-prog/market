package com.medusa.gruul.addon.distribute.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.distribute.model.enums.DistributeOrderStatus;
import com.medusa.gruul.addon.distribute.mp.entity.DistributorOrder;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author 张治保 date 2022/11/19
 */
@Getter
@Setter
@ToString
public class DistributorOrderQueryDTO extends Page<DistributorOrder> {

    /**
     * 当前用户id 仅当 消费者端查询时生效
     */
    private Long currentUserId;

    /**
     * 关键词(针对 订单号/商品名称) 仅当 消费者端查询时生效
     */
    private String keywords;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 买家昵称
     */
    private String buyerNickname;

    /**
     * 下单时间 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 下单时间 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 订单状态
     */
    private DistributeOrderStatus status;
    /**
     * 分销商手机号
     */
    private String distributorPhone;
    /**
     * 是否是内购
     */
    private Boolean purchase;

    private Set<String> orderNos;
}
