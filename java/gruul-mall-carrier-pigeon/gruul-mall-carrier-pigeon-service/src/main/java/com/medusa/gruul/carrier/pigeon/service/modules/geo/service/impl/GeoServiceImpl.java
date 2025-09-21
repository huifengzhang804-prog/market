package com.medusa.gruul.carrier.pigeon.service.modules.geo.service.impl;


import com.medusa.gruul.carrier.pigeon.service.modules.geo.model.constant.GeoConstant;
import com.medusa.gruul.carrier.pigeon.service.modules.geo.model.dto.GeoConfigDTO;
import com.medusa.gruul.carrier.pigeon.service.modules.geo.service.GeoService;
import com.medusa.gruul.common.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 *
 * @author xiaoq
 * date 2025/02/10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeoServiceImpl implements GeoService {
    /**
     * 重置高德配置信息
     *
     * @param geoConfig 高德配置信息
     */
    @Override
    public void editGeoConf(GeoConfigDTO geoConfig) {
        RedisUtil.setCacheMap(RedisUtil.key(GeoConstant.GEO_KEY),geoConfig);
    }

    @Override
    public GeoConfigDTO getGeoConf() {
        return RedisUtil.getCacheMap(RedisUtil.key(GeoConstant.GEO_KEY), GeoConfigDTO.class);
    }
}
