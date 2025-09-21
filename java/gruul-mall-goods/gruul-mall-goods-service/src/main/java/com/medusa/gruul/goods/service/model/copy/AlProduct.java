package com.medusa.gruul.goods.service.model.copy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author miskw
 * @date 2023/2/3
 * @describe 阿里data JSOn对象
 */
@Data
public class AlProduct {

    private String title;
    private List<String> images = List.of();
    private Map<String, Sku> skuMap = Map.of();
    private List<String> videoUrl;
    private List<AlSku> skuProps = List.of();
    private String desc;
    private List<String> descImgs;
    private List<ShowPriceJsonDto> showPriceRanges = List.of();


    @Getter
    @Setter
    @ToString
    public static class Sku {

        /**
         * 原价
         */
        private BigDecimal price;

        /**
         * 销售价
         */
        private BigDecimal discountPrice;

        /**
         * 库存
         */
        private BigInteger canBookCount;

        /**
         * 销量
         */
        private BigInteger saleCount;
    }

    @Data
    public static class ShowPriceJsonDto {

        /**
         * 起始数量
         */
        private BigInteger startAmount;
        /**
         * 价钱
         */
        private BigDecimal price;

        /**
         *
         */
        private BigInteger beginAmount;
    }
}
