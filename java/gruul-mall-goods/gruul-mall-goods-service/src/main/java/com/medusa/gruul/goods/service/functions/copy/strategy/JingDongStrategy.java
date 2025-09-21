package com.medusa.gruul.goods.service.functions.copy.strategy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.service.model.copy.JdProduct;
import com.medusa.gruul.goods.service.model.copy.JdSku;
import com.medusa.gruul.goods.service.model.vo.CopyProductVO;
import com.medusa.gruul.goods.service.model.vo.CopySkuVO;
import com.medusa.gruul.storage.api.dto.SpecDTO;
import com.medusa.gruul.storage.api.dto.SpecGroupDTO;
import com.medusa.gruul.storage.api.enums.LimitType;
import com.medusa.gruul.storage.api.enums.StockType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * 京东商品采集策略
 *
 * @author 张治保
 * @since 2024/4/11
 */
@Slf4j
public class JingDongStrategy extends AbstractProductCollectorStrategy {

    @Override
    protected CopyProductVO getProduct(JSONObject data) {
        JSONObject item = data.getJSONObject(GoodsConstant.ITEM);
        JdProduct jdData = item.to(JdProduct.class);
        CopyProductVO product = new CopyProductVO();
        product.setName(jdData.getName());
        product.setOriginalImgUrls(jdData.getImages());
        product.setPic(CollUtil.isEmpty(product.getOriginalImgUrls()) ? null
                : product.getOriginalImgUrls().get(CommonPool.NUMBER_ZERO));
//        product.setDetail(StrUtil.emptyIfNull(jdData.getDesc()).replaceAll("data-lazyload=", "src="));
        product.setDetail(dealDetail(jdData.getDesc()));
        sku(product, jdData);
        return product;
    }

    //处理商品详情
    private String dealDetail(String desc) {
        List<String> imgList = Lists.newArrayList();
        //提取不同详情格式的图片
        if (desc.contains("data-lazyload")) {
            imgList = dealLazyLoad(desc);
        } else if (desc.startsWith("<style>.")) {
            imgList = dealStyle(desc);
        }
        if (CollectionUtil.isEmpty(imgList)) {
            //兜底处理
            return desc;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<p>");
        imgList.forEach(img -> {
            sb.append("<img src=\"").append(img).append("\">");
        });
        sb.append("</p>");
        return sb.toString();
    }

    /**
     * 提取详情图片
     *
     * @param desc
     * @return
     */
    private List<String> dealStyle(String desc) {
        String regex = "background-image\\s*:\\s*url\\(([^)]+)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(desc);

        // 用于存储提取的URL
        List<String> imgList = new ArrayList<>();

        // 提取匹配的URL
        while (matcher.find()) {
            // 去掉引号
            String url = matcher.group(1).replaceAll("^['\"]|['\"]$", "");
            imgList.add(url);
        }
        return imgList;
    }

    /**
     * 提取详情图片
     *
     * @param desc
     * @return
     */
    private List<String> dealLazyLoad(String desc) {
        // 正则表达式
        String imgRegex = "data-lazyload=\"(https?://[^\"]+)\"";
        Pattern pattern = Pattern.compile(imgRegex);
        Matcher matcher = pattern.matcher(desc);
        List<String> imgList = Lists.newArrayList();
        // 查找并打印所有匹配的URL
        while (matcher.find()) {
            imgList.add(matcher.group(1));
        }
        return imgList;
    }

    /**
     * 京东规格组与 sku 处理
     */
    private void sku(CopyProductVO product, JdProduct data) {
        //解析规格组数据
        List<SpecGroupDTO> specGroups = getSpecGroups(data);
        product.setSpecGroups(specGroups);
        //规格组为空 标识为单规格商品
        if (CollUtil.isEmpty(specGroups)) {
            product.setSkus(
                    List.of(
                            new CopySkuVO()
                                    .setStockType(StockType.UNLIMITED)
                                    .setInitStock((long) CommonPool.NUMBER_ZERO)
                                    .setInitSalesVolume(CommonPool.NUMBER_ZERO)
                                    .setLimitType(LimitType.UNLIMITED)
                                    .setLimitNum(CommonPool.NUMBER_ZERO)
                                    .setImage(product.getPic())
                                    .setPrice(data.getOriginalPrice())
                                    .setSalePrice(data.getPrice())
                                    .setWeight(BigDecimal.ZERO)
                                    .setMinimumPurchase(CommonPool.NUMBER_ONE)
                    )
            );
            return;
        }
        //sku 数据解析
        List<CopySkuVO> skus = data.getSku()
                .stream()
                .map(JdSku::from)
                .map(
                        jdSku -> new CopySkuVO()
                                .setSpecs(jdSku.getSpecs().values().stream().map(this::subSpecName).toList())
                                .setInitStock((long) CommonPool.NUMBER_ZERO)
                                .setStockType(StockType.LIMITED)
                                .setInitSalesVolume(CommonPool.NUMBER_ZERO)
                                .setLimitType(LimitType.UNLIMITED)
                                .setLimitNum(CommonPool.NUMBER_ZERO)
                                .setImage(jdSku.getImagePath())
                                .setPrice(jdSku.getOriginalPrice())
                                .setSalePrice(jdSku.getPrice())
                                .setWeight(BigDecimal.ZERO)
                                .setMinimumPurchase(CommonPool.NUMBER_ONE)
                ).toList();
        product.setSkus(skus);
    }

    /**
     * 解析规格组数据
     *
     * @param data 京东商品数据
     * @return 规格组数据
     */
    private List<SpecGroupDTO> getSpecGroups(JdProduct data) {
        TreeMap<Integer, String> saleProp = data.getSaleProp();
        if (CollUtil.isEmpty(saleProp)) {
            return List.of();
        }
        Map<Integer, List<String>> skuProps = data.getSkuProps();
        List<SpecGroupDTO> specGroups = new ArrayList<>();
        saleProp.forEach(
                (groupIndex, name) -> {
                    List<SpecDTO> specs = new ArrayList<>();
                    List<String> specNames = skuProps.get(groupIndex);
                    for (int idx = 0; idx < specNames.size(); idx++) {
                        String specName = specNames.get(idx);
                        SpecDTO spec = new SpecDTO();
                        spec.setName(subSpecName(specName));
                        spec.setOrder(idx);
                        specs.add(spec);
                    }
                    SpecGroupDTO group = new SpecGroupDTO();
                    group.setName(subGroupName(name));
                    group.setOrder(groupIndex);
                    group.setChildren(specs);
                    specGroups.add(group);
                }
        );
        return specGroups;
    }


    @Override
    protected String url(String goodsUrl) {
        if (StrUtil.isEmpty(goodsUrl) || !goodsUrl.contains(GoodsConstant.HTML_SUFFIX)) {
            return null;
        }
        return goodsUrl.substring(goodsUrl.lastIndexOf(StrUtil.SLASH) + 1, goodsUrl.indexOf(GoodsConstant.HTML_SUFFIX));
    }
}
