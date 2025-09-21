package com.medusa.gruul.addon.supplier.mp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.model.base.ProductSkuKey;
import com.medusa.gruul.common.mp.handler.type.MapToKeyValuesTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * 入库操作记录
 *
 * @author 张治保
 * date 2023/7/27
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "t_supplier_storage_record", autoResultMap = true)
public class SupplierStorageRecord implements Serializable {


    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 店铺 id
     */
    private Long shopId;

    /**
     * 供应商id
     */
    private Long supplierId;

    /**
     * 入库备注
     */
    private String remark;


    /**
     * 入库记录
     */
    @TableField(typeHandler = MapToKeyValuesTypeHandler.class)
    private Map<ProductSkuKey, Integer> skuRecords;
    

}
