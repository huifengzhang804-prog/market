package com.medusa.gruul.shop.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.shop.api.entity.Shop;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 张治保
 * @since 2024/3/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopSearchParamDTO extends Page<Shop> {

    /**
     * 是否是用户主动搜索
     */
    private Boolean userSearch = Boolean.TRUE;

    /**
     * 商户名称
     */
    private String name;

    /**
     * 店铺类型
     */
    private ShopType shopType;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 指定店铺 id 查询 并按 列表排序
     */
    private List<Long> shopIds;

    /**
     * 有商品的店铺
     */
    private Boolean productIsNotEmpty = Boolean.FALSE;


    /**
     * 查询销量前几的商品数据
     * eg.0 表示 不查询销量排行的商品数据
     * eg.5 标识查询商品销量排行前五的数据
     */
    private Integer salesRanking = 0;


    /**
     * 是否查询店铺评分
     */
    private Boolean scored = Boolean.FALSE;


}
