package com.medusa.gruul.afs.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.afs.api.enums.RefundType;
import com.medusa.gruul.afs.service.model.dto.BuyerReturnedDTO;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.order.api.enums.DeliverType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * <p>
 * 售后物流运输
 * </p>
 *
 * @author 张治保
 * @since 2022-08-03
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_afs_package", autoResultMap = true)
public class AfsPackage extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 售后工单号
     */
    private String afsNo;

    /**
     * 配送方式
     */
    private DeliverType type;

    /**
     * 收货人名称
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverMobile;

    /**
     * 收货人详细地址
     */
    private String receiverAddress;


    /**
     * 退货类型
     */
    private RefundType refundType;


    /**
     * 退货信息
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private BuyerReturnedDTO buyerReturnedInfo;
}
