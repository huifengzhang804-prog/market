package com.medusa.gruul.search.service.es.entity;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.goods.api.enums.ImageRatioEnum;
import com.medusa.gruul.goods.api.model.enums.PricingType;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.search.api.enums.BrandStatus;
import com.medusa.gruul.search.api.model.SearchCouponVO;
import com.medusa.gruul.search.service.model.vo.SearchMemberInfoVO;
import com.medusa.gruul.storage.api.enums.StockType;
import com.medusa.gruul.storage.api.vo.ProductSkusVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.easyes.annotation.*;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.annotation.rely.FieldStrategy;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 张治保 date 2022/11/30
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@IndexName(value = "product", keepGlobalPrefix = true)
public class EsProductEntity extends EsBaseEntity {

    /**
     * es中的唯一id
     */
    @IndexId(type = IdType.CUSTOMIZE)
    private String id;

    /**
     * 店铺id
     */
    @IndexField(fieldType = FieldType.LONG)
    private Long shopId;

    /**
     * 店铺名称
     */
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.PINYIN)
    private String shopName;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品名称
     */
    @HighLight(mappingField = "highLight", preTag = "<em z>")
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.PINYIN, searchAnalyzer = Analyzer.PINYIN)
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
     * 图片比例
     */
    private ImageRatioEnum imageRatio;


    /**
     * skuId列表
     */
    @IndexField(fieldType = FieldType.LONG)
    private List<Long> skuIds;

    /**
     * 规格列表 与 skuIds 下标 一一对应
     */
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_MAX_WORD)
    private List<String> specs;

    /**
     * 原价（划线价） 规格列表 与 skuIds 下标 一一对应
     */
    @IndexField(fieldType = FieldType.LONG)
    private List<Long> prices;

    /**
     * 实售价 规格列表 与 skuIds 下标 一一对应
     */
    @IndexField(fieldType = FieldType.LONG)
    private List<Long> salePrices;

    /**
     * 规格列表 与 skuIds 下标 一一对应
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private List<StockType> stockTypes;

    /**
     * 规格列表 与 skuIds 下标 一一对应
     */
    @IndexField(fieldType = FieldType.LONG)
    private List<Long> stocks;

    /**
     * 总销量
     */
    @IndexField(fieldType = FieldType.LONG)
    private Long salesVolume;

    /**
     * 商品配送方式
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private List<DistributionMode> distributionMode;

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
    @IndexField(fieldType = FieldType.LONG)
    private Long platformCategoryFirstId;

    /**
     * 平台二级类目id
     */
    @IndexField(fieldType = FieldType.LONG)
    private Long platformCategorySecondId;

    /**
     * 平台三级类目id
     */
    @IndexField(fieldType = FieldType.LONG)
    private Long platformCategoryThirdId;

    /**
     * 品牌id
     */
    @IndexField(strategy = FieldStrategy.IGNORED)
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
    @Score
    private Float searchScore;

    /**
     * 高亮商品名称
     */
    @IndexField(exist = false)
    private String highLight;

    /**
     * 是否分销商品(默认false)
     */
    @IndexField(strategy = FieldStrategy.IGNORED)
    private Boolean isDistributed = Boolean.FALSE;

    /**
     * 卖点描述
     */
    private String saleDescribe;

    /**
     * 商品标签
     */
    @IndexField(fieldType = FieldType.NESTED, nestedClass = EsProductLabel.class)
    private EsProductLabel productLabel;

    /**
     * 运费模块id，0-商家包邮
     */
    @IndexField(strategy = FieldStrategy.IGNORED)
    private Long freightTemplateId;

    /**
     * 店铺类型
     */
    private ShopType shopType;

    /**
     * 优惠券
     */
    @IndexField(exist = false)
    private List<SearchCouponVO> couponList;

    /**
     * 历史店铺描述
     */
    @IndexField(exist = false)
    private String shopOperationHistory;

    /**
     * 会员价标签
     */
    @IndexField(exist = false)
    private SearchMemberInfoVO memberInfo;
    /**
     * sku信息
     */
    @IndexField(exist = false)
    private List<ProductSkusVO.SkuVO> skuVOS ;

    /**
     * skuId 对应 下标 map
     *
     * @return skuId 对应 下标 map
     */
    public Map<Long, Integer> skuIdIndexMap() {
        List<Long> skuIds = getSkuIds();
        if (CollUtil.isEmpty(skuIds)) {
            return Collections.emptyMap();
        }
        Map<Long, Integer> skuIdIndexMap = new HashMap<>(skuIds.size() * 4 / 3 + 1);
        int index = 0;
        for (Long skuId : skuIds) {
            skuIdIndexMap.put(skuId, index);
            index++;
        }
        return skuIdIndexMap;
    }

}
