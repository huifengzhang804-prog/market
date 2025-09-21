package com.medusa.gruul.goods.service.functions.copy.strategy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.service.model.copy.TbProduct;
import com.medusa.gruul.goods.service.model.copy.TbSpec;
import com.medusa.gruul.goods.service.model.copy.TbSpecGroup;
import com.medusa.gruul.goods.service.model.vo.CopyProductVO;
import com.medusa.gruul.goods.service.model.vo.CopySkuVO;
import com.medusa.gruul.storage.api.dto.SpecDTO;
import com.medusa.gruul.storage.api.dto.SpecGroupDTO;
import com.medusa.gruul.storage.api.enums.LimitType;
import com.medusa.gruul.storage.api.enums.StockType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 淘宝 商品采集策略
 *
 * @author 张治保
 * @since 2024/4/11
 */
public class TaoBaoStrategy extends AbstractProductCollectorStrategy {

    @Override
    protected CopyProductVO getProduct(JSONObject data) {
        JSONObject item = data.getJSONObject(GoodsConstant.ITEM);
        item.getJSONArray(GoodsConstant.SKU).remove(CommonPool.NUMBER_ZERO);
        TbProduct tbData = item.to(TbProduct.class);
        CopyProductVO product = new CopyProductVO();
        product.setName(tbData.getTitle());
        product.setOriginalImgUrls(tbData.getImages());
        if (CollUtil.isNotEmpty(tbData.getDescImgs())) {
            product.setDetail(dealDescImgs(tbData.getDescImgs()));
        } else {
            product.setDetail(tbData.getDesc());
        }

        List<String> videos = tbData.getVideos();
        if (CollUtil.isNotEmpty(videos)) {
            product.setVideoUrl(videos.get(CommonPool.NUMBER_ZERO));
        }
        sku(product, tbData);
        return product;
    }

    /**
     * 京东规格组与 sku 处理
     */
    private void sku(CopyProductVO product, TbProduct data) {
        Map<String, Integer> groupOrderMap = MapUtil.newHashMap();
        //处理规格组
        List<SpecGroupDTO> specGroups = new ArrayList<>();
        List<TbSpecGroup> props = data.getProps();
        for (int index = 0; index < props.size(); index++) {
            TbSpecGroup tbGroup = props.get(index);
            SpecGroupDTO group = new SpecGroupDTO();
            String groupName = tbGroup.getName();

            group.setName(subGroupName(groupName));
            group.setOrder(index);
            List<SpecDTO> specs = new ArrayList<>();
            List<TbSpec> tbSpecs = tbGroup.getValues();
            for (int idx = 0; idx < tbSpecs.size(); idx++) {
                SpecDTO spec = new SpecDTO();
                String name = tbSpecs.get(idx).getName();
                spec.setName(subSpecName(name));
                spec.setOrder(idx);
                specs.add(spec);
            }
            group.setChildren(specs);
            specGroups.add(group);
            groupOrderMap.put(groupName, index);
        }
        product.setSpecGroups(specGroups);

        List<CopySkuVO> skus = data
                .getSku()
                .stream()
                .map(tbSku -> {
                    CopySkuVO sku = new CopySkuVO();
                    sku.setSpecs(specs(groupOrderMap, tbSku.getSkuName()));
                    sku.setInitStock(tbSku.getQuantity().longValue());
                    sku.setStockType(StockType.LIMITED);
                    sku.setInitSalesVolume(CommonPool.NUMBER_ZERO);
                    sku.setLimitType(LimitType.UNLIMITED);
                    sku.setLimitNum(CommonPool.NUMBER_ZERO);
                    sku.setImage(tbSku.getImage());
                    sku.setPrice(tbSku.getPrice());
                    sku.setSalePrice(tbSku.getPrice());
                    sku.setWeight(BigDecimal.ZERO);
                    return sku;
                }).toList();
        product.setSkus(skus);
    }

    /**
     * sku 规格信息处理 并按照规格组顺序排序
     *
     * @param groupOrderMap    规格组顺序
     * @param originalSpecsStr 原始规格字符串 如: 颜色分类--驼色;尺码--S
     * @return 规格信息
     */
    private List<String> specs(Map<String, Integer> groupOrderMap, String originalSpecsStr) {
        //处理的数据数据为 [颜色分类--驼色, 尺码--S]
        return StrUtil.split(originalSpecsStr, GoodsConstant.SEMICOLON)
                .stream()
                .map(originalSpec -> {
                    //[颜色分类,驼色]
                    //[尺码,S]
                    List<String> split = StrUtil.split(originalSpec, GoodsConstant.DOUBLE_DASHED);
                    String groupName = split.get(CommonPool.NUMBER_ZERO);
                    return new OrderedSpec(split.get(CommonPool.NUMBER_ONE), groupOrderMap.get(groupName));
                })
                .sorted(Comparator.comparing(OrderedSpec::getOrder))
                .map(group -> subSpecName(group.getSpecName()))
                .toList();
    }

    @Override
    protected String url(String goodsUrl) {
        Map<String, List<String>> stringListMap = HttpUtil.decodeParams(goodsUrl, (String) null);
        List<String> ids = stringListMap.get(GoodsConstant.ID);
        return CollUtil.isEmpty(ids) ? null : ids.get(CommonPool.NUMBER_ZERO);
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    private static class OrderedSpec {

        private String specName;
        private Integer order;
    }
}
