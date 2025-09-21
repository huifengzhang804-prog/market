package com.medusa.gruul.search.service.model;


import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.easyes.core.biz.OrderByParam;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


/**
 * 检索参数
 *
 * @author WuDi
 * date 2022/9/28
 */
@Data
@Accessors(chain = true)
public class SearchParam {

    /**
     * 指定 id 查询，并按照这个顺序排序
     */
    private List<String> ids;

    /**
     * 自定义排序
     */
    private List<OrderByParam> orderByParams;
    /**
     * 页面传递过来的全文匹配关键字
     */
    private String keyword;
    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 最小价格
     */
    private Long minPrice;
    /**
     * 最大价格
     */
    private Long maxPrice;
    /**
     * 商品id
     */
    private Set<Long> productId;
    /**
     * 需要排除的商品id集合
     */
    private Set<Long> excludeProductIds;
    /**
     * 品牌id
     */
    private Set<Long> brandId;
    /**
     * 一级分类id
     */
    private Long categoryFirstId;
    /**
     * 二级分类id
     */
    private Long categorySecondId;
    /**
     * 三级分类id
     */
    private Long categoryThirdId;
    /**
     * 平台一级类目id
     */
    private Long platformCategoryFirstId;
    /**
     * 平台二级类目id
     */
    private Long platformCategorySecondId;
    /**
     * 平台三级类目id
     */
    private Long platformCategoryThirdId;
    /**
     * 页码
     */
    private Integer current = 1;
    /**
     * 条数
     */
    @Max(value = 500, message = "最大条数不能超过500")
    private Integer size = 10;
    /**
     * 活动检索
     */
    @Valid
    private ForActivity activity;

    /**
     * 检索代销商品
     * false 仅检索代销商品  true 检索所有商品
     * todo 字段定义有问题  应该是为 true 的时候仅检索代销商品
     */
    private Boolean searchConsignmentProduct = Boolean.TRUE;

    /**
     * 检索商品为分销商品
     */
    private Boolean distributeProduct = Boolean.FALSE;

    /**
     * 检索商品总库存大于0
     */
    private Boolean searchTotalStockGtZero = Boolean.FALSE;

    /**
     * 客户端是否展示优惠券
     */
    private Boolean showCoupon = Boolean.FALSE;

    /**
     * 检索商品sku信息
     */
    private Boolean showSku = Boolean.FALSE;

    /**
     * 客户端是否展示历史店铺浏览操作
     * 排序规则：
     * 买过的店（order）、关注的店（goods）、逛过的店（user 商品足迹）、搜过的店
     */
    private Boolean showHistory = Boolean.FALSE;

    /**
     * 配送方式
     */
    private DistributionMode distributionMode;
    /**
     * 排除的商品 用于商品详情页排除当前正在查看的商品
     */
    private Long excludeProductId;


    @Getter
    @Setter
    @ToString
    public static class ForActivity implements Serializable {
        /**
         * 活动类型
         */
        private OrderType activityType;

        /**
         * 活动开始时间
         */
        @NotNull
        private LocalDateTime startTime;

        /**
         * 活动结束时间
         */
        @NotNull
        private LocalDateTime endTime;
    }

}
