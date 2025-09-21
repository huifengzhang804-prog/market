package com.medusa.gruul.goods.api.model.vo;

import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.global.model.enums.ServiceBarrier;
import com.medusa.gruul.goods.api.entity.ProductCategory;
import com.medusa.gruul.goods.api.entity.ProductLabel;
import com.medusa.gruul.goods.api.model.dto.ProductExtraDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import com.medusa.gruul.storage.api.dto.StorageSpecSkuDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 商品信息Vo
 * @Author: xiaoq
 * @Date : 2022-03-10 10:25
 */

@Data
@Accessors(chain = true)
public class ProductVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1308276745007998779L;

    /**
     * 商品 id
     */
    private Long id;

    /**
     * 店铺 id
     */
    private Long shopId;
    /**
     * 供应商id(S2B2C)
     */
    private Long supplierId;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 供应商id
     */
    private Long providerId;
    /**
     * 类目id
     */
    private Long categoryId;
    /**
     * 平台类目id
     */
    private Long platformCategoryId;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 供应商名称
     */
    private String providerName;
    /**
     * 配送方式(0--商家配送，1--快递配送 2--同城配送)
     */
    private List<DistributionMode> distributionMode;
    /**
     * 运费模板ID
     */
    private Long freightTemplateId;
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
    private Integer sale;
    /**
     * 画册图片，连产品图片限制为6张，以逗号分割
     */
    private String albumPics;
    /**
     * 视频url
     */
    private String videoUrl;
    /**
     * 状态(默认上架，0--下架，1--上架)
     */
    private ProductStatus status;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 服务保障
     */
    private List<ServiceBarrier> serviceIds;
    /**
     * 商品详情
     */
    private String detail;
    /**
     * 评分
     */
    private BigDecimal score;
    /**
     * 规格是否展开
     */
    private Boolean openSpecs;
    /**
     * 卖点描述
     */
    private String saleDescribe;
    /**
     * 销售价格
     */
    private List<Long> salePrices;
    /**
     * 商品价格
     */
    private Long salePrice;

    /**
     * 预估价格
     */
    private Long estimatedPrice;
    /**
     * 商品仓储
     */
    private List<StorageSku> storageSkus;
    /**
     * 商品展示分类信息
     */
    private ProductCategory productCategory;
    /**
     * 附加数据
     */
    private ProductExtraDTO extra;
    /**
     * 商品类型
     */
    private ProductType productType;
    /**
     * 销售类型
     */
    private SellType sellType;

    /**
     * sku 信息
     */
    private List<StorageSpecSkuDTO> storageSpecSku;

    /**
     * 采集地址
     */
    private String collectionUrl;

    /**
     * 商品标签ID
     */
    private Long labelId;
    /**
     * 商品标签
     */
    private ProductLabel productLabel;

}

