package com.medusa.gruul.storage.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.storage.api.enums.LimitType;
import com.medusa.gruul.storage.api.enums.StockType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;


/**
 * 商品 仓储 表
 *
 * @author 张治保
 * @since 2022-06-21
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode(of = {"activityType", "activityId", "shopId", "productId"}, callSuper = true)
@TableName(value = "t_storage_sku", autoResultMap = true)
public class StorageSku extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 库存类型
     */
    private OrderType activityType;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 库存类型 1 无限库存 2 有限
     */
    private StockType stockType;

    /**
     * 商品库存
     */
    private Long stock;

    /**
     * 销量
     */
    private Long salesVolume;

    /**
     * 初始销量
     */
    private Long initSalesVolume;

    /**
     * 限购类型 1 不限购 , 2 商品限购 , 3 规格限购
     */
    private LimitType limitType;

    /**
     * 限购数量
     */
    private Integer limitNum;

    /**
     * 规格名称列表
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> specs;

    /**
     * sku图片
     */
    private String image;

    /**
     * 原价 单位 豪 1豪 = 0.01分
     */
    private Long price;

    /**
     * 真实销售价 单位豪 1豪 = 0.01分
     */
    private Long salePrice;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 最低购买量
     */
    private Integer minimumPurchase;

}
