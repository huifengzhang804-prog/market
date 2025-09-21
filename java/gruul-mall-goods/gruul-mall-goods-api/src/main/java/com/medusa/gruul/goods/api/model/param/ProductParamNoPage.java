package com.medusa.gruul.goods.api.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.goods.api.enums.ProductSortEnum;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import lombok.Data;

/**
 * @Description: 商品查询Param
 * @Author: xiaoq
 * @Date : 2022-03-10 10:20
 */
@Data
public class ProductParamNoPage  {

    private Long id;

    /**
     * 类目id
     */
    private Long categoryId;

    /**
     * 商品名称
     */
    private String name;
    /**
     * 状态(默认上架，0--下架，1--上架 )
     */
    private ProductStatus status;

    /**
     * 创建开始时间
     */
    private String createBeginTime;
    /**
     * 创建结束时间
     */
    private String createEndTime;
    /**
     * 最低价
     */
    private Long lowestPrice;

    /**
     * 最高价
     */
    private Long highestPrice;

    /**
     * 商品类型
     */
    private ProductType productType;

    /**
     * 销售类型
     */
    private SellType sellType;
    /**
     * 二级平台类目id
     */
    private Long secondPlatformCategoryId;
    /**
     * 排序
     */
    private ProductSortEnum sort;
    /**
     * 店铺id
     */
    private Long shopId;
}
