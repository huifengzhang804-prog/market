package com.medusa.gruul.goods.api.model.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.medusa.gruul.goods.api.model.enums.ProductAuditStatus;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 审核商品VO
 *
 * @author xiaoq
 * @Description AuditProductVO.java
 * @date 2023-09-26 13:21
 */
@Data
@Accessors(chain = true)
public class AuditProductVO implements Serializable {

    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品状态
     */
    @JSONField(serialize = false)
    private ProductStatus status;


    /**
     * 商品审核状态
     */
    private ProductAuditStatus auditStatus;


    /**
     * 审核时间
     */
    private LocalDateTime auditTime;


    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

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
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品主图
     */
    private String pic;


    /**
     * 商品名称
     */
    private String shopName;


    /**
     * 拒绝说明
     */
    private String explain;
    /**
     * 商品类型
     */
    private ProductType productType;
}
