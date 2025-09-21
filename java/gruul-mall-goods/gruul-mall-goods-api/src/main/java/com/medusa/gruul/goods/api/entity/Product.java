package com.medusa.gruul.goods.api.entity;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.global.model.enums.ServiceBarrier;
import com.medusa.gruul.goods.api.model.dto.ConsignmentPriceSettingDTO;
import com.medusa.gruul.goods.api.model.dto.ProductExtraDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品信息表
 *
 * @author xiaoq
 * @since 2022-03-04
 */
@Data
@EqualsAndHashCode(of = {"shopId"}, callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_product", autoResultMap = true)
public class Product extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 供应商id
     */
    @TableField("provider_id")
    private Long providerId;


    /**
     * 供应商id(S2B2C)
     */
    @TableField("supplier_id")
    private Long supplierId;

    /**
     * 运费模板id
     */
    @TableField("freight_template_id")
    private Long freightTemplateId;

    /**
     * 分类id
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 平台类目id
     */
    @TableField("platform_category_id")
    private Long platformCategoryId;

    /**
     * 品牌id
     */
    @TableField("brand_id")
    private Long brandId;

    /**
     * 店铺id
     */
    @TableField("shop_id")
    private Long shopId;

    /**
     * 商品名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 标签id
     */
    @TableField("label_id")
    private Long labelId;

    /**
     * 展示图片
     */
    @TableField("pic")
    private String pic;

    /**
     * 宽屏展示图片
     */
    @TableField("wide_pic")
    private String widePic;

    /**
     * 画册图片，连产品图片限制为6张，以逗号分割
     */
    @TableField("album_pics")
    private String albumPics;


    /**
     * 视频url
     */
    @TableField("video_url")
    private String videoUrl;

    /**
     * 状态(默认上架，ProductStatus)
     */
    @TableField("`status`")
    private ProductStatus status;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;
    /**
     * 总库存
     */
    @TableField("total_stock")
    private Long totalStock;
    /**
     * 总销量
     */
    @TableField("total_sales_volume")
    private Long totalSalesVolume;


    /**
     * 商品价格
     */
    private Long salePrice;

    /**
     * 商品类型
     */
    private ProductType productType;

    /**
     * 服务保障["ServiceBarrier","ServiceBarrier"]
     */
    @TableField(value = "service_ids", typeHandler = Fastjson2TypeHandler.class)
    private List<ServiceBarrier> serviceIds;

    /**
     * 商品详情
     */
    @TableField("detail")
    private String detail;

    /**
     * 规格是否展开
     */
    @TableField("is_open_specs")
    private Integer openSpecs;


    /**
     * 卖点描述
     */
    @TableField("sale_describe")
    private String saleDescribe;

    /**
     * 采集地址
     */
    @TableField("collection_url")
    private String collectionUrl;

    /**
     * 评分
     */
    @TableField("score")
    private BigDecimal score;


    /**
     * 销售类型
     */
    @TableField("sell_type")
    private SellType sellType;


    /**
     * 配送方式(0--商家配送，1--快递配送，2--同城配送，3--门店)
     */
    @TableField(value = "distribution_mode", typeHandler = Fastjson2TypeHandler.class)
    private List<DistributionMode> distributionMode;


    @TableField(value = "sale_prices", typeHandler = Fastjson2TypeHandler.class)
    private List<Long> salePrices;


    /**
     * 附加数据
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private ProductExtraDTO extra;

    /**
     * 商品标签
     */
    @TableField(exist = false)
    private ProductLabel productLabel;

    /**
     * 店铺类型
     */
    @TableField(exist = false)
    private ShopType shopType;

    /**
     * 店铺名称
     */
    @TableField(exist = false)
    private String shopName;

    /**
     * 是否是支持的配送方式
     *
     * @param distributionMode 配送方式
     * @return boolean 是否支持 true 支持 false 不支持
     */
    public boolean isSupportDistributionMode(DistributionMode distributionMode) {
        return CollUtil.contains(getDistributionMode(), distributionMode);
    }

    /**
     * 重新计算商品销售价格  如果是代销商品 则计算出代销价格
     *
     * @return 销售价格
     */
    public List<Long> realSalePrice() {
        List<Long> salePrices = this.getSalePrices();
        if (CollUtil.isEmpty(salePrices)) {
            return List.of();
        }
        //计算代销价
        if (SellType.CONSIGNMENT != this.getSellType()) {
            return salePrices;
        }
        ProductExtraDTO extra = this.getExtra();
        if (extra == null) {
            return List.of();
        }
        ConsignmentPriceSettingDTO consignmentPriceSetting = extra.getConsignmentPriceSetting();
        if (consignmentPriceSetting == null) {
            return List.of();
        }
        return salePrices.stream()
                .map(salePrice -> consignmentPriceSetting.singlePrice(salePrice).getSalePrice())
                .toList();
    }


}
