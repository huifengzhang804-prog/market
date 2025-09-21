package com.medusa.gruul.goods.api.model.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.goods.api.entity.ProductLabel;
import com.medusa.gruul.goods.api.model.dto.ProductExtraDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 商品查看VO
 *
 * @author xiaoq
 * @Description LookProductVO.java
 * @date 2023-09-25 14:23
 */
@Data
@Accessors(chain = true)
public class LookProductVO {

    /**
     * 商品id
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 采集地址
     */
    private String collectionUrl;

    /**
     * 商品标签id
     */
    private Long labelId;


    /**
     * 店铺类目名称
     */
    private CategoryLevelName shopCategoryName;


    /**
     * 平台类目名称
     */
    private CategoryLevelName platformCategoryName;


    /**
     * 商品仓储
     */
    private List<StorageSku> storageSkus;


    /**
     * 商品详情
     */
    private String detail;


    /**
     * 商品 销售类型
     */
    private SellType sellType;


    /**
     * 供应商id
     */
    private Long supplierId;

    /**
     * 商品标签
     */
    private ProductLabel productLabel;

    /**
     * 附加数据
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    @JSONField(serialize = false)
    private ProductExtraDTO extra;


}
