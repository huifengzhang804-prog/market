package com.medusa.gruul.goods.service.model.vo;

import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.goods.api.entity.Product;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 关注的店铺
 *
 * @author wudi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopFollowVO {


    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 发布时间
     */
    private LocalDateTime createTime;
    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺logo
     */
    private String logo;

    /**
     * 上新提示
     */
    private String newTips;
    /**
     * 商品总数量
     */
    private Long productTotal;
    /**
     * 店铺下的图片
     */
    private List<Product> productList;

    /**
     * 已评价人数
     */
    private Long evaluated;

    /**
     * 上新时间
     */
    private LocalDateTime goodsCreateTime;

    /**
     * 店铺类型
     */
    private ShopType shopType;

    /**
     * 加购数
     */
    private Integer buyMore;

    /**
     * 浏览量
     */
    private Long pv;
}
