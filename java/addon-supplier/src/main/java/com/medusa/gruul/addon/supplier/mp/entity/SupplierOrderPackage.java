package com.medusa.gruul.addon.supplier.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.supplier.model.enums.PackageStatus;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.global.model.express.ExpressCompanyDTO;
import com.medusa.gruul.global.model.express.UserAddressDTO;
import com.medusa.gruul.order.api.enums.DeliverType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 张治保
 * date 2023/7/25
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "t_supplier_order_package", autoResultMap = true)
public class SupplierOrderPackage extends BaseEntity {

    /**
     * 供应商 id
     */
    private Long supplierId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 包裹状态
     */
    @TableField("`status`")
    private PackageStatus status;

    /**
     * 配送类型
     */
    private DeliverType type;

    /**
     * 快递信息
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private ExpressCompanyDTO express;

    /**
     * 收货人信息
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private UserAddressDTO receiver;


    /**
     * 该包裹对应的订单商品项
     */
    @TableField(exist = false)
    private List<SupplierOrderItem> orderItems;

}
