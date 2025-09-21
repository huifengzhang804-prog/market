package com.medusa.gruul.addon.freight.config;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.kuaidi100.sdk.api.QueryTrack;
import com.medusa.gruul.addon.freight.constant.FreightConstant;
import com.medusa.gruul.common.redis.util.RedisUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 持久化省市区配置到redis
 *
 * @author 张治保
 * date 2022/8/10
 */
@Configuration
public class FreightConfig {

    @Bean
    public QueryTrack queryTrack() {
        QueryTrack queryTrack = new QueryTrack();
        queryTrack.setTimeOut(6000, 5000);
        return queryTrack;
    }

    @Bean
    public CommandLineRunner chinaAreaDataPersistence() {
        return args -> {
            JSONObject chinaAreaJsonData = JSONUtil.parseObj(ResourceUtil.readStr("element-china-area-data.json", CharsetUtil.CHARSET_UTF_8));

            JSONObject provincesJsonData = JSONUtil.parseObj(ResourceUtil.readStr("provinces.json", CharsetUtil.CHARSET_UTF_8));
            JSONObject citysJsonData = JSONUtil.parseObj(ResourceUtil.readStr("citys.json", CharsetUtil.CHARSET_UTF_8));
            JSONObject areasJsonData = JSONUtil.parseObj(ResourceUtil.readStr("areas.json", CharsetUtil.CHARSET_UTF_8));
            RedisUtil.setCacheMap(FreightConstant.CHINA_AREA_CACHE_KEY, chinaAreaJsonData);

            RedisUtil.setCacheMap(FreightConstant.CHINA_PROVINCES_CACHE_KEY, provincesJsonData);
            RedisUtil.setCacheMap(FreightConstant.CHINA_CITYS_CACHE_KEY, citysJsonData);
            RedisUtil.setCacheMap(FreightConstant.CHINA_AREAS_CACHE_KEY, areasJsonData);
        };
    }
}
