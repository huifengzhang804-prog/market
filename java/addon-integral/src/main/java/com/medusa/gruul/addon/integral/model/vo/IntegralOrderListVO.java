package com.medusa.gruul.addon.integral.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.integral.model.constant.IntegralConstant;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderDeliverType;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderStatus;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import com.medusa.gruul.order.api.entity.OrderTimeout;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分订单vo
 *
 * @author shishuqian
 * date 2023/2/3
 * time 16:37
 **/

@Data
public class IntegralOrderListVO {

    private Long id;
    /**
     * 订单号
     */
    private String no;
    /**
     * 买家用户id
     */
    private Long buyerId;


    /**
     * 买家昵称
     */
    private String buyerNickname;

    /**
     * 卖家备注
     */
    private String sellerRemark;

    /**
     * 订单状态
     */
    private IntegralOrderStatus status;


    /**
     * 积分商品名称
     */
    private String productName;


    private Long productId;


    /**
     * 积分价格
     */
    private Long price;
    /**
     * 非积分价格
     */
    private Long salePrice;

    /**
     * 积分商品主图
     */
    private String image;

    /**
     * 购买数量
     */
    private Integer num = IntegralConstant.INTEGRAL_PRODUCT_MAX_BUY_NUM;

    /**
     * 运费金额
     */
    private Long freightPrice;

    private IntegralOrderReceiverListVO integralOrderReceiverVO;

    private LocalDateTime createTime;

    /**
     * 关键节点超时时间 key_node_timeout
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private OrderTimeout timeout;

    /**
     * 积分订单的发货方式
     */
    private IntegralOrderDeliverType integralOrderDeliverType;


    /**
     * 物流公司编号
     */
    private String expressCompanyName;

    /**
     * 物流公司名称
     */
    private String expressName;


    /**
     * 包裹运单号
     */
    private String expressNo;

    /**
     * 所属平台
     */
    private Platform affiliationPlatform;

    /**
     * 商品类型
     */
    private ProductType productType;
}
