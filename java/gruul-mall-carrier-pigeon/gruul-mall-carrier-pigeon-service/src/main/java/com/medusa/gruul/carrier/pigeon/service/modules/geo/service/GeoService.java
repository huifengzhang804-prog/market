package com.medusa.gruul.carrier.pigeon.service.modules.geo.service;


import com.medusa.gruul.carrier.pigeon.service.modules.geo.model.dto.GeoConfigDTO;
import jakarta.validation.Valid;

/**
 *
 * @author xiaoq
 * date 2025/02/10
 */
public interface GeoService {
    /**
     * 重置高德配置信息
     *
     * @param geoConfig 高德配置信息
     */
    void editGeoConf(@Valid GeoConfigDTO geoConfig);

    /**
     * 获取高德配置信息
     *
     * @return 高德配置信息
     */
    GeoConfigDTO getGeoConf();

}
