package com.medusa.gruul.carrier.pigeon.service.modules.geo.controller;

import com.medusa.gruul.carrier.pigeon.service.modules.geo.model.dto.GeoConfigDTO;
import com.medusa.gruul.carrier.pigeon.service.modules.geo.service.GeoService;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.dto.OssConfigDto;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.model.resp.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Geo配置控制器
 *
 * @author xiaoq
 * @since 2025-01-20 14:22
 */
@Slf4j
@RestController
@RequestMapping("/geo")
@RequiredArgsConstructor
public class GeoController {

    private  final GeoService geoService;

    /**
     * 保存或更新Geo配置
     *
     * @param geoConfig 高德配置信息
     */
    @Idem(1000)
    @PostMapping("/edit")
    @PreAuthorize("@S.platformPerm('generalSet')")
    public Result<Void> editGeoConf(@RequestBody @Valid GeoConfigDTO geoConfig) {
        geoService.editGeoConf(geoConfig);
        return Result.ok();
    }

    /**
     * 获取 高德配置
     * 
     * @return 高德配置信息
     */
    @PostMapping("/conf")
    @PreAuthorize("permitAll()")
    public Result<GeoConfigDTO> getGeoConf(){
        return Result.ok(geoService.getGeoConf());
    }
}
