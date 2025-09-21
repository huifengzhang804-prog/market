package com.medusa.gruul.goods.service.model.param;

import com.medusa.gruul.goods.api.enums.CopyGoodsType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;


/**
 * @author miskw
 * @date 2023/1/30
 * @describe 一键复制dto
 */
@Data
public class CopyGoodsParam {
    /**
     * TaoBao 淘宝
     * JD 京东
     * AliBaBa 阿里
     */
    @NotNull(message = "平台类型不能为空!")
    private CopyGoodsType copyGoodsType;

    /**
     * 复制的原URL链接
     */
    @NotBlank(message = "商品链接地址不能为空!")
    @URL(message = "商品链接地址格式不正确!")
    private String goodsUrl;

}
