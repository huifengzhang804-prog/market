package com.medusa.gruul.goods.api.model.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.goods.api.entity.ProductLabel;
import com.medusa.gruul.goods.api.model.dto.ProductExtraDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import com.medusa.gruul.storage.api.entity.StorageSku;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 平台商品信息Vo
 * @Author: xiaoq
 * @Date : 2022-04-21 14:58
 */
@Data
public class PlatformProductVO implements Serializable {

    private Long id;
    /**
     * 商品名称
     */
    private String name;

    /**
     * 供应商名称
     */
    private String providerName;

    /**
     * 供应商id(S2B2C)
     */
    private Long supplierId;


    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 商品状态
     */
    private ProductStatus productStatus;

    /**
     * 卖点描述
     */
    private String saleDescribe;

    /**
     * 主图展示图片
     */
    private String pic;

    /**
     * 画册图片，连产品图片限制为6张，以逗号分割
     */
    private String albumPics;

    /**
     * 商品 仓储
     */
    private List<StorageSku> storageSkus;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 销售价格
     */
    private List<Long> salePrices;

    /**
     * 最大价格
     */
    private Long maxPrice;
    /**
     * 最小价格
     */
    private Long minPrice;
    /**
     * 销售价格
     */
    private Long salePrice;

    /**
     * 销售类型
     */
    private SellType sellType;

    private Long labelId;

    private ProductLabel productLabel;
    /**
     * 商品类型
     */
    private ProductType productType;

    /**
     * 附加数据
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private ProductExtraDTO extra;

    @JSONField(serialize = false)
    private ActivityShopProductKey activityShopProductKey;
}
