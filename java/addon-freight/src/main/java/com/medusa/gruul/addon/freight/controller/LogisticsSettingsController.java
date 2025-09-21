package com.medusa.gruul.addon.freight.controller;

import com.medusa.gruul.addon.freight.constant.FreightConstant;
import com.medusa.gruul.addon.freight.model.dto.LogisticsSettingsDTO;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsSettings;
import com.medusa.gruul.addon.freight.service.FreightService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.redis.util.RedisUtil;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 物流设置控制层
 *
 * @author xiaoq
 * @Description
 * @date 2022-08-12 10:26
 */
@RestController
@RequestMapping("logistics/settings")
@PreAuthorize("""
                @S.matcher()
                .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN,@S.R.SUPER_CUSTOM_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN)
                .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'goods:list','freight:logistics'))
                .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.ROLES,'generalSet'))
                .match()
        """)
@RequiredArgsConstructor
public class LogisticsSettingsController {

    private final FreightService freightService;


    /**
     * 物流设置编辑
     *
     * @param logisticsSettingsDTO 物流设置dto
     * @return Result.ok()
     */
    @Idem(500)
    @PostMapping("/edit")
    public Result<Void> addLogisticsSettings(@RequestBody @Valid LogisticsSettingsDTO logisticsSettingsDTO) {
        freightService.addLogisticsSettings(logisticsSettingsDTO);
        return Result.ok();
    }


    /**
     * 获取物流设置
     *
     * @return Result<LogisticsSettings>
     */
    @GetMapping("/get")
    public Result<LogisticsSettings> getLogisticsSettings() {
        return Result.ok(freightService.getLogisticsSettings());
    }


    @PreAuthorize("permitAll()")
    @GetMapping("/zone")
    public Result<Map<String, Object>> getZone(String zone) {
        Map<String, Object> cacheMap = RedisUtil.getCacheMap(
                FreightConstant.CHINA_BASE_CACHE_KEY.concat(":").concat(zone));
        return Result.ok(cacheMap);
    }
}
