package com.medusa.gruul.storage.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.storage.service.model.dto.SkuStockItemDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * 仓储管理订单项信息
 *
 * @author xiaoq
 * @Description StorageManagementOrderItem.java
 * @date 2023-07-26 09:36
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_storage_management_order_item", autoResultMap = true)
public class StorageManagementOrderItem extends BaseEntity {

    /**
     * 所属id
     */
    private Long shopId;

    /**
     * 订单号
     */
    private String orderNo;


    /**
     * 产品id
     */
    private Long productId;


    /**
     * 商品主图
     */
    private String productName;


    /**
     * 商品主图
     */
    private String pic;


    /**
     * 库存消费记录 dto
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<SkuStockItemDTO> skuStockItems;


}
