package com.medusa.gruul.addon.distribute.controller;

import com.medusa.gruul.addon.distribute.model.dto.DistributeConfDTO;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeConf;
import com.medusa.gruul.addon.distribute.service.DistributeConfHandleService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 分销配置控制器
 *
 * @author 张治保
 * date 2022/11/15
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/distribute/config")
public class DistributeConfController {

    private final DistributeConfHandleService distributeConfHandleService;


    /**
     * 查询分销配置
     *
     * @return 分销配置
     */
    @Log("查询分销配置")
    @PreAuthorize("permitAll()")
    @GetMapping
    public Result<DistributeConf> config() {
        return Result.ok(
                distributeConfHandleService.config().getOrNull()
        );
    }

    /**
     * 修改分销配置
     *
     * @param distributeConf 分销配置
     * @return void
     */
    @Idem(500)
    @Log("修改分销配置")
    @PutMapping
    @PreAuthorize("@S.platformPerm('distribution:index')")
    public Result<Void> updateConf(@RequestBody @Valid DistributeConfDTO distributeConf) {
        distributeConf.validParam();
        distributeConfHandleService.updateConf(distributeConf);
        return Result.ok();
    }


}
