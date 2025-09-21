package com.medusa.gruul.goods.api.model.dto;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.global.model.enums.ServiceBarrier;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import com.medusa.gruul.goods.api.model.enums.GoodsError;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import com.medusa.gruul.storage.api.dto.SkuDTO;
import com.medusa.gruul.storage.api.dto.SpecDTO;
import com.medusa.gruul.storage.api.dto.SpecGroupDTO;
import io.vavr.control.Option;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 新增或修改产品信息DTO
 *
 * @author xiaoq
 * @since 2022-05-06
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 供应商id
     */
    private Long providerId;

    /**
     * 供应商id(S2B2C)
     */
    private Long supplierId;

    /**
     * 配送方式(0--商家配送，1--快递配送，2--同城配送,3--门店)
     */
    @NotNull
    @Size(min = 1)
    private List<DistributionMode> distributionMode;
    /**
     * 运费模板ID
     */
    @NotNull(message = "运费模板ID不可为空")
    private Long freightTemplateId;


    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 店铺类目
     */
    private CategoryLevel shopCategory;

    /**
     * 平台类目
     */
    @NotNull
    @Valid
    private CategoryLevel platformCategory;
    /**
     * 商品名称
     */
    @Size(max = 35, message = "商品名称过长")
    @NotBlank(message = "商品名称不可为空")
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
     * 画册图片，连产品图片限制为6张，以逗号分割
     */
    @NotNull
    private String albumPics;


    /**
     * 视频url
     */
    private String videoUrl;
    /**
     * 状态(默认上架)
     */
    private ProductStatus status;

    /**
     * 服务保障枚举ids
     */
    private List<ServiceBarrier> serviceIds;
    /**
     * 商品详情
     */
    private String detail;
    /**
     * 规格是否展开
     */
    private Boolean openSpecs;

    /**
     * 卖点描述
     */
    private String saleDescribe;

    /**
     * 标签id
     */
    private Long labelId;

    /**
     * 采集地址
     */
    @Size(max = 1024)
    private String collectionUrl;

    /**
     * 评分
     */
    private BigDecimal score;

    /**
     * 商品类型
     */
    @NotNull
    private ProductType productType;

    /**
     * 销售类型
     */
    @NotNull
    private SellType sellType;


    /**
     * 规格组
     */
    @Valid
    @Size(max = 5, message = "规格组不能超过5个")
    private List<SpecGroupDTO> specGroups;
    /**
     * sku列表
     */
    @Valid
    @NotNull
    @Size(min = 1)
    private List<SkuDTO> skus;


    /**
     * 产品属性
     */
    @Valid
    private List<ProductFeaturesValueDTO> productAttributes;

    /**
     * 产品参数
     */
    @Valid
    private List<ProductFeaturesValueDTO> productParameters;

    /**
     * 价格设置
     */
    private ConsignmentPriceSettingDTO consignmentPriceSetting;

    /**
     * 供应商自定以折扣百分比
     */
    private Long supplierCustomDeductionRatio;


    public Product coverProduct() {
        Product product = new Product();
        if (this.getProductType() == ProductType.VIRTUAL_PRODUCT) {
            GoodsError.VIRTUAL_DISTRIBUTION_CHECK_ERROR.falseThrow(distributionMode.equals(Collections.singletonList(DistributionMode.VIRTUAL)));
        } else {
            GoodsError.REAL_DISTRIBUTION_CHECK_ERROR.trueThrow(distributionMode.contains(DistributionMode.VIRTUAL));
        }
        if (shopCategory == null || shopCategory.areFieldsNonNull()) {
            throw new GlobalException("店铺类目信息为必传,请检查相关参数");
        }
        BeanUtil.copyProperties(this, product);
        product.setCategoryId(Option.of(getShopCategory()).map(CategoryLevel::getThree).getOrNull())
                .setPlatformCategoryId(Option.of(getPlatformCategory()).map(CategoryLevel::getThree).getOrNull());
        return product;
    }

    public void checkDistributionMode(ShopMode mode) {
        if (mode == ShopMode.O2O && getDistributionMode().contains(DistributionMode.EXPRESS)) {
            throw new GlobalException("o2o模式不可包含快递配送");
        }
    }

    public void checkSpecGroups() {
        boolean specNames = specGroups.stream()
                .flatMap(specGroup -> specGroup.getChildren().stream())
                .map(SpecDTO::getName)
                .collect(Collectors.groupingBy(name -> name, Collectors.counting()))
                .entrySet()
                .stream()
                .anyMatch(entry -> entry.getValue() > 1);
        if (specNames) {
            throw new GlobalException("规格不能重名");
        }
    }

}

