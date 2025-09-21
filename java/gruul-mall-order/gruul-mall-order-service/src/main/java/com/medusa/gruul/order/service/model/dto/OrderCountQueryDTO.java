package com.medusa.gruul.order.service.model.dto;

import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.global.model.o.RangeDate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * @author 张治保
 * @since 2024/7/9
 */
@Getter
@Setter
@ToString
public class OrderCountQueryDTO {


    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 买家昵称
     */
    private String buyerNickname;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 查询指定供应商的商品
     */
    private Long supplierId;

    /**
     * 开始时间
     */
    private RangeDate time = new RangeDate();

    /**
     * 店铺ids 供查询条件店铺类型使用
     */
    private Set<Long> shopIds;

    /**
     * 配送方式
     */
    private DistributionMode distributionMode;

    /**
     * 销售类型
     */
    private SellType sellType;

    /**
     * 订单所属渠道
     */
    private Platform platform;

    /**
     * 小程序发货提示
     */
    private Boolean isMiniAppTip = Boolean.FALSE;
}
