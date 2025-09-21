package com.medusa.gruul.goods.service.functions.copy.strategy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.service.model.copy.AlProduct;
import com.medusa.gruul.goods.service.model.copy.AlSku;
import com.medusa.gruul.goods.service.model.vo.CopyProductVO;
import com.medusa.gruul.goods.service.model.vo.CopySkuVO;
import com.medusa.gruul.storage.api.dto.SpecDTO;
import com.medusa.gruul.storage.api.dto.SpecGroupDTO;
import com.medusa.gruul.storage.api.enums.LimitType;
import com.medusa.gruul.storage.api.enums.StockType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 阿里巴巴 1688 商品采集策略
 *
 * @author 张治保
 * @since 2024/04/10
 */
@Slf4j
public class Alibaba1688Strategy extends AbstractProductCollectorStrategy {

    @Override
    public CopyProductVO getProduct(JSONObject data) {
        AlProduct aliData = JSON.parseObject(data.toString(), AlProduct.class);
        CopyProductVO product = new CopyProductVO();
        product.setName(aliData.getTitle());
        List<String> images = aliData.getImages();
        product.setOriginalImgUrls(images);
        product.setPic(CollUtil.isEmpty(images) ? null : images.get(CommonPool.NUMBER_ZERO));

        List<String> videoUrl = aliData.getVideoUrl();
        if (CollUtil.isNotEmpty(videoUrl)) {
            product.setVideoUrl(videoUrl.get(CommonPool.NUMBER_ZERO));
        }
        if (CollUtil.isNotEmpty(aliData.getDescImgs())) {
            product.setDetail(dealDescImgs(aliData.getDescImgs()));
        } else {
            product.setDetail(aliData.getDesc());
        }
        //

        sku(product, aliData);
        return product;
    }


    /**
     * 阿里sku处理
     * todo 部分规格 没有对应的SKU 这种情况需要额外处理
     */
    @SuppressWarnings("all")
    private void sku(CopyProductVO product, AlProduct data) {
        BigDecimal showPrice = data.getShowPriceRanges().get(CommonPool.NUMBER_ZERO).getPrice();
        List<AlSku> skuProps = data.getSkuProps();
        //规格组为空 标识为单规格商品
        if (CollUtil.isEmpty(skuProps)) {
            product.setSpecGroups(List.of());
            product.setSkus(
                    List.of(
                            new CopySkuVO()
                                    .setPrice(showPrice)
                                    .setSalePrice(showPrice)
                                    .setImage(product.getPic())
                                    .setSpecs(List.of())
                                    .setStockType(StockType.UNLIMITED)
                                    .setInitStock((long) CommonPool.NUMBER_ZERO)
                                    .setInitSalesVolume(CommonPool.NUMBER_ZERO)
                                    .setLimitType(LimitType.UNLIMITED)
                                    .setLimitNum(CommonPool.NUMBER_ZERO)
                                    .setWeight(BigDecimal.ZERO)
                    )
            );
            return;
        }
        //解析规格组数据
        Map<String, String> imgMap = MapUtil.newHashMap();
        List<SpecGroupDTO> specGroups = new ArrayList<>(skuProps.size());
        for (int index = 0; index < skuProps.size(); index++) {
            AlSku skuProp = skuProps.get(index);
            SpecGroupDTO group = new SpecGroupDTO();
            //规格组名
            String groupName = skuProp.getProp();
            group.setName(subGroupName(groupName));
            List<AlSku.AlPropDto> value = skuProp.getValue();
            List<SpecDTO> children = new ArrayList<>(value.size());
            //规格列表
            for (int idx = 0; idx < value.size(); idx++) {
                AlSku.AlPropDto curSpec = value.get(idx);
                SpecDTO spec = new SpecDTO();
                //规格值 限制 28个字符
                String aliSpecName = curSpec.getName();
                spec.setName(subSpecName(aliSpecName));
                if (curSpec.getImageUrl() != null) {
                    imgMap.put(aliSpecName, curSpec.getImageUrl());
                }
                spec.setOrder(idx);
                children.add(spec);
            }
            group.setChildren(children);
            group.setOrder(index);
            specGroups.add(group);
        }
        //设置规格组信息
        product.setSpecGroups(specGroups);

        //解析 sku 信息
        Map<String, AlProduct.Sku> skuMap = data.getSkuMap();
        List<CopySkuVO> skus = new ArrayList<>(skuMap.size());

        skuMap.forEach((key, sku) -> {
            //原价
            BigDecimal price = sku.getPrice();
            price = price == null ? showPrice : price;
            //销售价
            BigDecimal discountPrice = sku.getDiscountPrice();
            CopySkuVO copySku = new CopySkuVO();
            copySku.setPrice(price);
            copySku.setSalePrice(ObjectUtil.defaultIfNull(discountPrice, price));
            //规格与 sku 图片
            SpecsImg specsImg = specs(key, imgMap);
            copySku.setImage(ObjectUtil.defaultIfNull(specsImg.getImage(), product.getPic()));
            copySku.setSpecs(specsImg.getSpecs());
            copySku.setInitStock(sku.getCanBookCount().longValue());
            copySku.setInitSalesVolume(sku.getSaleCount().intValue());
            copySku.setLimitType(LimitType.UNLIMITED);
            copySku.setStockType(StockType.LIMITED);
            copySku.setLimitNum(CommonPool.NUMBER_ZERO);
            copySku.setWeight(BigDecimal.ZERO);
            skus.add(copySku);
        });
        product.setSkus(skus);
    }

    /**
     * 规格处理 与 图片处理 按照规格组顺序排序
     *
     * @param originalSpecsStr 原始规格字符串 深空灰&gt;2023款 i5-1340P/16GB/1T
     * @param imgMap           规格图片
     * @return 规格与图片
     */
    private SpecsImg specs(String originalSpecsStr, Map<String, String> imgMap) {
        List<String> orderedSpecs = StrUtil.split(originalSpecsStr, XmlUtil.GT);
        List<String> specs = new ArrayList<>(orderedSpecs.size());
        String image = null;
        for (String spec : orderedSpecs) {
            String img = imgMap.get(spec);
            if (image == null && StrUtil.isNotBlank(img)) {
                image = img;
            }
            specs.add(subSpecName(spec));
        }
        return new SpecsImg(specs, image);
    }


    @Override
    protected String url(String goodsUrl) {
        if (!goodsUrl.contains(GoodsConstant.HTML_SUFFIX)) {
            return null;
        }
        int beginIndex = goodsUrl.lastIndexOf(StrUtil.SLASH) + 1;
        int endIndex = goodsUrl.indexOf(GoodsConstant.HTML_SUFFIX);
        if (beginIndex >= endIndex) {
            //处理url参数中带有/的问题
            goodsUrl = goodsUrl.substring(0, goodsUrl.indexOf("?"));
            beginIndex = goodsUrl.lastIndexOf(StrUtil.SLASH) + 1;
            endIndex = goodsUrl.indexOf(GoodsConstant.HTML_SUFFIX);
        }
        return goodsUrl.substring(beginIndex, endIndex);
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    private static class SpecsImg {

        private List<String> specs;
        private String image;
    }


}