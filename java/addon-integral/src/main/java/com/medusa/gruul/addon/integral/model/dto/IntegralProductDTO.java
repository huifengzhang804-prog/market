package com.medusa.gruul.addon.integral.model.dto;

import com.medusa.gruul.addon.integral.mp.entity.IntegralProduct;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import io.vavr.control.Option;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * 积分商品DTO
 *
 * @author xiaoq
 * @Description 积分商品dto
 * @date 2023-01-31 16:25
 */
@Getter
@Setter
@ToString
public class IntegralProductDTO {

    private Long id;


    /**
     * 积分商品名称
     */
    @NotNull
    @Size(max = 35)
    private String name;

    /**
     * 市场价格
     */
    private BigDecimal price;

    /**
     * 积分价
     */
    @NotNull
    private Long integralPrice;

    /**
     * 混合支付金额
     */
    private Long salePrice;


    /**
     * 库存
     */
    @NotNull
    private Integer stock;

    /**
     * 虚拟销量
     */
    @NotNull
    private Integer virtualSalesVolume;

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
    @NotBlank
    private String albumPics;

    /**
     * 运费金额
     */
    @NotNull
    private BigDecimal freightPrice;


    /**
     * 商品属性信息
     */
    private List<IntegralProductAttribute> integralProductAttributes;

    /**
     * 商品类型
     */
    @NotNull
    private ProductType productType;

    public IntegralProduct coverIntegralProduct() {
        IntegralProduct integralProduct = new IntegralProduct();
        integralProduct.setName(this.name);
        integralProduct.setIntegralPrice(this.integralPrice);
        integralProduct.setSalePrice(Option.of(this.salePrice).getOrElse(0L));
        integralProduct.setStock(this.stock);
        integralProduct.setPrice(this.price);
        // 实物商品支持运费 虚拟商品不支持运费
        integralProduct.setFreightPrice(this.productType == ProductType.VIRTUAL_PRODUCT ? BigDecimal.ZERO : this.freightPrice);
        integralProduct.setVirtualSalesVolume(this.virtualSalesVolume == null ? 0 : this.virtualSalesVolume);
        integralProduct.setDetail(this.detail);
        integralProduct.setPic(this.pic);
        integralProduct.setAlbumPics(this.albumPics);
        integralProduct.setIntegralProductAttributes(integralProductAttributes);
        integralProduct.setProductType(this.productType);

        return integralProduct;
    }


}
