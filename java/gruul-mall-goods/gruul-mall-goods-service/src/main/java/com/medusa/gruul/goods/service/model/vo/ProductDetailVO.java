package com.medusa.gruul.goods.service.model.vo;

import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.global.model.enums.ServiceBarrier;
import com.medusa.gruul.goods.api.entity.ProductCategory;
import com.medusa.gruul.goods.api.model.dto.ProductExtraDTO;
import com.medusa.gruul.goods.api.model.enums.DiscountType;
import com.medusa.gruul.goods.api.model.enums.EarningType;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import com.medusa.gruul.goods.api.model.vo.ActivityDetailVO;
import com.medusa.gruul.goods.api.model.vo.ProductDiscountVO;
import com.medusa.gruul.goods.api.model.vo.SetMealBasicInfoVO;
import com.medusa.gruul.storage.api.vo.ProductSpecsSkusVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品详情聚合参数
 *
 * @author 张治保
 * @since 2024/6/24
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductDetailVO implements Serializable {

    /**
     * 店铺 id
     */
    private Long shopId;

    /**
     * 商品 id
     */
    private Long productId;

    /**
     * 当前sku id
     */
    private Long skuId;

    /**
     * 商品类型
     */
    private ProductType productType;
    /**
     * 商品分类 即店铺类目的第三级分类
     */
    private ProductCategory productCategory;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 展示图片
     */
    private String pic;

    /**
     * 宽屏展示图片
     */
    private String widePic;


    /**
     * 销量
     */
    private Long sale;

    /**
     * 支持的配送方式
     */
    private Set<DistributionMode> distributionMode;

    /**
     * 运费模板ID
     */
    private Long freightTemplateId;

    /**
     * 画册图片，连产品图片限制为6张，以逗号分割
     */
    private List<String> albumPics;

    /**
     * 视频url
     */
    private String videoUrl;

    /**
     * 服务保障
     */
    private Set<ServiceBarrier> serviceIds;

    /**
     * 买点描述
     */
    private String saleDescribe;

    /**
     * 商品详情
     */
    private String detail;

    /**
     * 附加数据
     */
    private ProductExtraDTO extra;

    /**
     * 销售类型
     */
    private SellType sellType;

    /**
     * 是否已关注商品
     */
    private Boolean whetherCollect = Boolean.FALSE;

    /**
     * 规格 sku 信息
     */
    private ProductSpecsSkusVO specsSkus;

    /**
     * 活动是否处于进行中
     */
    private Boolean activityOpen = Boolean.FALSE;

    /**
     * 当前 sku 是否参与活动
     */
    private Boolean skuActivity = Boolean.FALSE;

    /**
     * 商品活动信息
     */
    private ActivityDetailVO activity;

    /**
     * 当前 sku 的活动库存 当前当前活动进行中且sku参加活动时有值
     */
    private Long activityStock;

    /**
     * 套餐
     */
    private List<SetMealBasicInfoVO> packages = List.of();

    /**
     * 商品优惠信息
     */
    private Map<DiscountType, ProductDiscountVO> discountMap;

    /**
     * 商品预计赚、返金额
     */
    private Map<EarningType, Long> earningMap;

    /**
     * 是否可用叠加优惠  默认全部可用
     */
    private StackableDiscount stackable = new StackableDiscount()
            .setCoupon(true)
            .setVip(true)
            .setFull(true);

    /**
     * 商品价格计算逻辑
     */
    private ProductPriceVO price;
}

