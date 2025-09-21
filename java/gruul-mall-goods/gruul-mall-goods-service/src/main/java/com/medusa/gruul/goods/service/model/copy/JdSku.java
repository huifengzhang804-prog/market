package com.medusa.gruul.goods.service.model.copy;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@ToString
public class JdSku {
    /**
     * 规格
     */
    private Map<Integer, String> specs;
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    /**
     * 销售价格
     */
    private BigDecimal price;
    /**
     * sku图片
     */
    private String imagePath;

    public static JdSku from(JSONObject json) {
        JdSku sku = json.to(JdSku.class);
        //支持 15 个规格
        int groupCount = 15;
        Map<Integer, String> specs = MapUtil.newHashMap(true);
        for (int group = 1; group <= groupCount; group++) {
            String key = String.valueOf(group);
            String spec = json.getString(key);
            if (StrUtil.isEmpty(spec)) {
                break;
            }
            specs.put(group, spec);
        }
        sku.setSpecs(specs);
        return sku;

    }
}