package com.medusa.gruul.shop.service.model.param;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.goods.api.model.vo.ProductFirstCategoryVO;
import com.medusa.gruul.search.api.model.ProductVO;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类用于填充装修页面数据
 *
 * @author Andy.Yan
 */
public class FillingDecorationData {

    private static final String KEY_FORM_DATA = "formData";
    private static final String KEY_VALUE = "value";
    private static final String KEY_GOODS = "goods";
    /**
     * JSON Key
     **/
    private static final String KEY_FIRST_CATEGORY_LIST = "firstCatList";
    private static final String KEY_CATEGORY_LIST = "categoryList";
    private static final String KEY_SHOP_ID = "shopId";
    private static final String KEY_SEARCH = "search";
    private final List<ProductVO> productVOList;
    private final List<ProductFirstCategoryVO> categoryVOList;
    private final String shopID;
    /**
     * 装修properties(JSON格式)
     */
    @Getter
    private final JSONArray properties;

    /**
     * 初始化{@link FillingDecorationData}对象，根据页面类型填充相应的数据到{@link FillingDecorationData#properties}
     *
     * @param productVOList  装修页面所对应的商品数据集合，参考{@link ProductVO}
     * @param categoryVOList 装修页面所对应的一级分类集合，参考{@link ProductFirstCategoryVO}
     * @param shopID         装修页面所对应的店铺ID
     * @param properties     装修页面的JSON表示
     */
    public FillingDecorationData(List<ProductVO> productVOList, List<ProductFirstCategoryVO> categoryVOList,
                                 String shopID, JSONArray properties) {
        this.productVOList = CollectionUtils.isEmpty(productVOList) ? new ArrayList<>() : productVOList;
        this.categoryVOList = CollectionUtils.isEmpty(categoryVOList) ? new ArrayList<>() : categoryVOList;
        this.shopID = shopID;
        this.properties = properties;
        if (properties == null || properties.size() == CommonPool.NUMBER_ZERO) {
            return;
        }
        // 填充数据
        for (int i = CommonPool.NUMBER_ZERO; i < properties.size(); i++) {
            JSONObject jsonObject = JSON.parseObject(properties.get(i).toString());
            JSONObject formData = JSON.parseObject(jsonObject.get(KEY_FORM_DATA).toString());
            if (jsonObject.get(KEY_VALUE) != null && jsonObject.get(KEY_VALUE).toString().equals(KEY_GOODS)) {
                formData.put(KEY_GOODS, JSONArray.parseArray(JSON.toJSONString(this.productVOList)));
                formData.put(KEY_FIRST_CATEGORY_LIST, JSONArray.parseArray(JSON.toJSONString(this.categoryVOList)));
            } else if (jsonObject.get(KEY_VALUE) != null && jsonObject.get(KEY_VALUE).toString().equals(KEY_SEARCH)) {
                formData.put(KEY_SHOP_ID, this.shopID);
            } else {
                formData.put(KEY_CATEGORY_LIST, JSONArray.parseArray(JSON.toJSONString(this.categoryVOList)));
            }
            jsonObject.put(KEY_FORM_DATA, formData);
            properties.set(i, jsonObject);
        }
    }
}
