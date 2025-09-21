package com.medusa.gruul.addon.ic.modules.uupt.util;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptConfig;
import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptResponse;
import com.medusa.gruul.addon.ic.modules.opens.uupt.api.IUuptApiFactory;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.order.OpenCityListResp;
import com.medusa.gruul.addon.ic.modules.uupt.model.UuptConstant;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.util.RedisUtil;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * @since 2024/8/27
 */
public interface UuptHelper {

    /**
     * UU 跑腿 开放平台开放城市列表缓存时间 30 天，存在更新的可能所以 ，30天后重新获取最新数据
     */
    long UUPT_OPEN_CITIES_CACHE_DAYS = 30;

    /**
     * 获取指定店铺的 openid
     *
     * @param shopId 店铺 id
     * @return 店铺对应的 openId
     */
    static String getOpenid(Long shopId) {
        return RedisUtil.getCacheMapValue(UuptConstant.UUPT_SHOP_OPENID_CACHE_KEY, shopId.toString());
    }

    /**
     * 设置店铺的 openid
     *
     * @param shopId 店铺 id
     * @param openId uupt openid
     */
    static void setOpenid(Long shopId, String openId) {
        RedisUtil.setCacheMapValue(UuptConstant.UUPT_SHOP_OPENID_CACHE_KEY, shopId.toString(), openId);
    }

    /**
     * 分转成豪
     *
     * @param price 价格 单位分
     * @return 豪
     */
    static long fenToHao(Long price) {
        return price == null ? 0 : (CommonPool.NUMBER_ONE_HUNDRED * price);
    }


    /**
     * 保存开放城市列表
     *
     * @param openCityList 开放城市列表
     * @return 已保存的开放城市列表
     */
    @SuppressWarnings("unchecked")
    static Set<String> saveOpenCities(OpenCityListResp openCityList) {
        if (openCityList == null) {
            return Set.of();
        }
        List<OpenCityListResp.City> cities = openCityList.getOpenCityList();
        if (cities == null || cities.isEmpty()) {
            return Set.of();
        }
        //开放城市名称列表
        //eg1. 宁波市
        //eg2. 南通市如东县
        //eg3. 黔南布依族苗族自治州瓮安县
        Set<String> openCities = cities
                .stream()
                .map(city -> {
                    String countyName = city.getCountyName();
                    if (StrUtil.isEmpty(countyName)) {
                        return city.getCityName();
                    }
                    return city.getCityName() + city.getCountyName();
                }).collect(Collectors.toSet());
        RedisUtil.executePipelined(
                redisTemplate -> {
                    BoundSetOperations<String, Object> setOperations = redisTemplate.boundSetOps(UuptConstant.UUPT_OPEN_CITY_CACHE_KEY);
                    setOperations.add(openCities.toArray(String[]::new));
                    setOperations.expire(Duration.ofDays(UUPT_OPEN_CITIES_CACHE_DAYS));
                }
        );
        return openCities;
    }

    /**
     * 当前城市是否已开放
     *
     * @param city   城市名
     * @param county 区县名
     * @return 是否已开放
     */
    static boolean cityIsOpen(String city, String county, IUuptApiFactory uuptApiFactory) {
        RedisTemplate<String, Object> redisTemplate = RedisUtil.getRedisTemplate();
        Boolean exists = redisTemplate.hasKey(UuptConstant.UUPT_OPEN_CITY_CACHE_KEY);
        //开放城市列表已过期重新获取城市列表
        if (BooleanUtil.isFalse(exists)) {
            if (uuptApiFactory == null) {
                return false;
            }
            UuptConfig config = uuptApiFactory.config();
            //未开启配置
            if (config == null || StrUtil.isEmpty(config.getAppid())) {
                return false;
            }
            // todo 暂不加锁
            UuptResponse<OpenCityListResp> response = uuptApiFactory.order().openCityList();
            if (!response.isSuccess()) {
                return false;
            }
            Set<String> openCities = saveOpenCities(response.getBody());
            return openCities.contains(city) || openCities.contains(county) || openCities.contains(city + county);
        }
        BoundSetOperations<String, Object> setOperations = redisTemplate
                .boundSetOps(UuptConstant.UUPT_OPEN_CITY_CACHE_KEY);
        //如果有一个存在即表示已开放
        Map<Object, Boolean> isMemeberMap = setOperations.isMember(city, county, city + county);
        if (isMemeberMap == null) {
            return false;
        }
        for (Boolean value : isMemeberMap.values()) {
            if (value) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成配送单号
     *
     * @return 配送单号
     */
    static String icNo() {
        return IdUtil.getSnowflakeNextIdStr();
    }
}
