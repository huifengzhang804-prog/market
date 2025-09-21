package com.medusa.gruul.goods.service.model.copy;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author miskw
 * @date 2023/2/2
 */
@Data
public class JdProduct {
    /**
     * 商品名称
     */
    private String name;
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    /**
     * 当前价
     */
    private BigDecimal price;
    /**
     * 规格名称
     */
    private TreeMap<Integer, String> saleProp = MapUtil.newTreeMap(Integer::compareTo);
    /**
     * 规格值
     * {"1": ["暗紫色","深空黑色","金色","银色"],
     * "2": ["128G","256G","512G","1TB"]}
     */
    private Map<Integer, List<String>> skuProps = MapUtil.newHashMap(true);
    /**
     * sku
     * {
     * "1": "暗紫色",
     * "2": "128G",
     * "stockState": 33,
     * "3": "",
     * "stockStateName": "现货",
     * "originalPrice": "7999.00",
     * "imagePath": "jfs/t1/150597/7/27516/21810/6343f8c8E952032fc/1f14645bd080c2c4.jpg",
     * "price": "7599.00",
     * "skuStatus": 1,
     * "skuId": 100042697319
     * }
     */

    private List<JSONObject> sku;
    /**
     * 图片
     */
    private List<String> images;
    /**
     * 描述
     */
    private String desc;
    

}
