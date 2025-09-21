package com.medusa.gruul.addon.integral.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderStatus;
import com.medusa.gruul.common.system.model.model.Platform;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 积分订单查询dto
 *
 * @author shishuqian
 * date 2023/2/3
 * time 15:03
 **/

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class IntegralOrderQueryDTO extends Page<Object> {
    /**
     * 积分订单的状态
     */
    private IntegralOrderStatus status;
    /**
     * 买家id
     */
    private Long buyerId;

    /**
     * 订单号
     */
    private String no;
    /**
     * 收货人姓名
     */
    private String consignee;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;
    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    /**
     * 所属渠道
     */
    private Platform affiliationPlatform;

    /**
     * 等待发货数量
     */
    private Long waitDeliveryNum;

}
