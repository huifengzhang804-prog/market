package com.medusa.gruul.cart.service.model.vo;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ConsignmentPriceDTO;
import com.medusa.gruul.goods.api.model.dto.ConsignmentPriceSettingDTO;
import com.medusa.gruul.goods.api.model.dto.FeatureValueDTO;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesValueDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 购物车商品sku详情
 *
 * @author 张治保
 * date 2022/5/16
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductSkuVO {

    /**
     * 该商品Sku的id
     */
    private Long id;

    /**
     * 唯一id(确定购物车同规格,同属性商品的唯一性)
     */
    private String uniqueId;

    /**
     * 选购的商品id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品主图
     */
    private String productImage;

    /**
     * 运费模板
     */
    private Long freightTemplateId;

    /**
     * 该商品Sku的图谝
     */
    private String image;

    /**
     * 该商品Sku销售价
     */
    private Long price;

    /**
     * 实售价
     */
    private Long salePrice;

    /**
     * 最终价
     */
    private Long finalPrice;

    /**
     * 购物车该商品的sku购买数量
     */
    private Integer num;

    /**
     * 规格 ['XL','红色']
     */
    private List<String> specs;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 限购
     */
    private Integer perLimit;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 商品限购与库存
     */
    private SkuStockVO skuStock;

    /**
     * 是否更新数量 当商品库存不足时需要用户重新调整购买数量
     */
    private Boolean needUpdateNum;

    /**
     * 商品销售类型
     */
    private SellType sellType;

    /**
     * 供应商id
     */
    private Long supplierId;

    /**
     * 配送方式(0--商家配送，1--快递配送，2--同城配送，3--门店)
     */
    private List<DistributionMode> distributionMode;

    /**
     * 产品属性
     */
    private Set<ProductFeaturesValueDTO> productAttributes;


    /**
     * 代销价格设置
     */
    private ConsignmentPriceSettingDTO consignmentPriceSetting;

    /**
     * 转成可编辑的对象
     */
    public ProductSkuEditedVO toEdit(Product product, StorageSku storage) {
        ProductSkuEditedVO edited = new ProductSkuEditedVO();
        edited.setProductId(getProductId())
                .setProductName(product.getName())
                .setProductImage(product.getPic())
                .setFreightTemplateId(product.getFreightTemplateId())
                .setId(getId())
                .setImage(getImage())
                .setPrice(getPrice())
                .setSalePrice(getSalePrice())
                .setNum(getNum())
                .setSupplierId(getSupplierId())
                .setSellType(getSellType())
                .setSpecs(getSpecs())
                .setStock(getStock())
                .setPerLimit(getPerLimit())
                .setDistributionMode(product.getDistributionMode())
                .setWeight(storage.getWeight());
        edited.setSkuStock(
                new SkuStockVO()
                        .setLimitType(storage.getLimitType())
                        .setLimitNum(storage.getLimitNum())
                        .setStockType(storage.getStockType())
                        .setStock(storage.getStock())
        );
        ConsignmentPriceSettingDTO consignmentPriceSetting = product.getExtra().getConsignmentPriceSetting();
        if (product.getSellType() == SellType.CONSIGNMENT && consignmentPriceSetting != null) {
            ConsignmentPriceDTO consignmentPrice = consignmentPriceSetting.singlePrice(storage.getSalePrice());
            storage.setSalePrice(consignmentPrice.getSalePrice());
            storage.setPrice(consignmentPrice.getPrice());
        }
        long sum = 0;
        if (CollUtil.isNotEmpty(product.getExtra().getProductAttributes())
                && CollUtil.isNotEmpty(getProductAttributes())) {
            //购物车中选择的服务与数据库中服务价格
            Set<Long> selectedFeatureIds = getProductAttributes().stream()
                    .flatMap(productFeaturesValue -> productFeaturesValue.getFeatureValues().stream())
                    .map(FeatureValueDTO::getFeatureValueId).collect(Collectors.toSet());
            sum = product.getExtra().getProductAttributes()
                    .stream()
                    .flatMap(productFeaturesValue -> productFeaturesValue.getFeatureValues().stream())
                    .filter(featureValueDTO -> selectedFeatureIds.contains(featureValueDTO.getFeatureValueId()))
                    .mapToLong(FeatureValueDTO::getSecondValue)
                    .sum();
        }
        edited.setFinalPrice(storage.getSalePrice() + sum);

        return edited;
    }

    /**
     * 转成不可用数据
     */
    public ProductSkuInvalidVO toInvalid() {
        ProductSkuInvalidVO invalid = new ProductSkuInvalidVO();
        invalid.setProductId(getProductId())
                .setProductName(getProductName())
                .setProductImage(getProductImage())
                .setFreightTemplateId(getFreightTemplateId())
                .setId(getId())
                .setImage(getImage())
                .setPrice(getPrice())
                .setSalePrice(getSalePrice())
                .setNum(getNum())
                .setSpecs(getSpecs())
                .setDistributionMode(getDistributionMode())
                .setWeight(getWeight());
        return invalid;
    }
}
