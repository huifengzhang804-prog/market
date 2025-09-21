package com.medusa.gruul.addon.rebate.controller;

import com.medusa.gruul.addon.rebate.model.dto.RebateConfDTO;
import com.medusa.gruul.addon.rebate.mp.entity.RebateConf;
import com.medusa.gruul.addon.rebate.service.RebateConfHandleService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 消费返利配置 前端控制器
 * </p>
 *
 * @author WuDi
 * @since 2023-07-17
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/rebateConf")
public class RebateConfController {

    private final RebateConfHandleService rebateConfHandleService;


    /**
     * 查询消费返利配置
     *
     * @return 消费返利配置
     */
    @Log("查询消费返利配置")
    @PreAuthorize("@S.platformPerm('consumerRebates') or @S.matcher().role(@S.USER).match()")
    @GetMapping
    public Result<RebateConf> config() {
        return Result.ok(
                rebateConfHandleService.config()
        );
    }


    /**
     * 编辑消费返利配置
     *
     * @param rebateConfDTO 消费返利配置
     */
    @Log("编辑消费返利配置")
    @PutMapping
    @PreAuthorize("@S.platformPerm('consumerRebates')")
    public Result<Void> editRebateConf(@RequestBody @Valid RebateConfDTO rebateConfDTO) {
        rebateConfHandleService.editRebateConf(rebateConfDTO);
        return Result.ok();
    }

}
