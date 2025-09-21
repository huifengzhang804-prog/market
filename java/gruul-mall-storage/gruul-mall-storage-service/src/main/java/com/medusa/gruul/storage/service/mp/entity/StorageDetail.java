package com.medusa.gruul.storage.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.storage.api.enums.StockChangeType;
import com.medusa.gruul.storage.api.enums.StockType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 仓储明细
 *
 * @author xiaoq
 * @Description StorageDetail.java
 * @date 2023-07-27 14:28
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_storage_detail", autoResultMap = true)
public class StorageDetail extends BaseEntity {

    /**
     * 产品id
     */
    private Long productId;

    /**
     * skuId
     */
    private Long skuId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 产品名称
     */
    private String productName;


    /**
     * 变化类型
     */
    private StockChangeType stockChangeType;

    /**
     * 变化数量
     */
    private Long stockChangeNum;

    /**
     * 销售类型
     */
    private SellType sellType;

    /**
     * 关联订单号
     */
    private String orderNo;

    /**
     * 变更人姓名
     */
    private String changeName;

    /**
     * 变更人手机号
     */
    private String changePhone;

    /**
     * 规格名称
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> specName;

    /**
     * 是否展示明细 只有为True的时候才会展示
     */
    private Boolean isShow;
    /**
     * 商品信息
     */
    @TableField(exist = false)
    private Product product;
    /**
     * sku库存类型
     */
    @TableField(exist = false)
    private StockType skuStockType;
}
