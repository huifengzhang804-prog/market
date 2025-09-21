package com.medusa.gruul.addon.team.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.List;

/**
 * 拼团活动商品关联表实体类
 *
 * @author 张治保
 */
@TableName(value = "t_team_product", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TeamProduct extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * sku Id
     */
    private Long skuId;

    /**
     * 库存
     */
    private Long stock;

    /**
     * 原销售价 todo 暂时无用 可能的拓展字段
     */
    @TableField(exist = false)
    private Long price;

    /**
     * 拼团价列表
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<Long> prices;


}
