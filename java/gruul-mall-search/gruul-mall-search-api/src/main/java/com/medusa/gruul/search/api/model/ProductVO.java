package com.medusa.gruul.search.api.model;

import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.goods.api.model.enums.PricingType;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.search.api.enums.BrandStatus;
import com.medusa.gruul.storage.api.enums.StockType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * <p></p>
 *
 * @author Andy.Yan
 */
@Data
@Accessors(chain = true)
public class ProductVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5426475117663958886L;

    /**
     * es中的唯一id
     */
    private String id;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品状态
     */
    private ProductStatus status;

    /**
     * 商品图片
     */
    private String pic;

    /**
     * 商品大图
     */
    private String widePic;


    /**
     * skuId列表
     */
    private List<Long> skuIds;

    /**
     * 规格列表 与 skuIds 下标 一一对应
     */
    private List<String> specs;

    /**
     * 原价 与 skuIds 下标 一一对应
     */
    private List<Long> prices;

    /**
     * 销售价 与 skuIds 下标 一一对应
     */
    private List<Long> salePrices;

    /**
     * 规格列表 与 skuIds 下标 一一对应
     */
    private List<StockType> stockTypes;
    /**
     * 规格列表 与 skuIds 下标 一一对应
     */
    private List<Long> stocks;

    /**
     * 总销量
     */
    private Long salesVolume;


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
     * 品牌id
     */
    private Long brandId;

    /**
     * 品牌状态
     */
    private BrandStatus brandStatus;

    /**
     * 商品销售类型
     */
    private SellType sellType;

    /**
     * 设价方式
     */
    private PricingType pricingType;
    /**
     * 销售价比值
     */
    private Long sale;
    /**
     * 划线价比值
     */
    private Long scribe;

    /**
     * 检索分
     */
    private Float searchScore;

    /**
     * 高亮商品名称
     */
    private String highLight;

    /**
     * 是否分销商品(默认false)
     */
    private Boolean isDistributed = Boolean.FALSE;
}
