package com.medusa.gruul.addon.integral.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.integral.model.dto.IntegralProductAttribute;
import com.medusa.gruul.addon.integral.model.enums.IntegralProductStatus;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 积分商品
 *
 * @author xiaoq
 * @Description 积分商品
 * @date 2023-01-31 14:23
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_integral_product", autoResultMap = true)
public class IntegralProduct extends BaseEntity {


    /**
     * 积分商品名称
     */
    private String name;

    /**
     * 市场价格
     */
    private BigDecimal price;

    /**
     * 积分价
     */
    private Long integralPrice;

    /**
     * 销售价
     */
    private Long salePrice;


    /**
     * 库存
     */
    private Integer stock;

    /**
     * 虚拟销量
     */
    private Integer virtualSalesVolume;

    /**
     * 真实销量
     */
    private Integer realitySalesVolume;

    /**
     * 商品详情
     */
    private String detail;


    /**
     * 展示主图
     */
    private String pic;

    /**
     * 画册图片
     */
    private String albumPics;

    /**
     * 积分商品状态
     */
    private IntegralProductStatus status;


    /**
     * 运费金额
     */
    private BigDecimal freightPrice;

    /**
     * 积分属性模板
     */
    @TableField(value = "integral_product_attribute", typeHandler = Fastjson2TypeHandler.class)
    private List<IntegralProductAttribute> integralProductAttributes;

    /**
     * 商品类型
     */
    @NotNull
    private ProductType productType;

}
