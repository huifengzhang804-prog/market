package com.medusa.gruul.addon.freight.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.freight.model.dto.LogisticsTemplateDTO;
import com.medusa.gruul.addon.freight.model.param.LogisticsTemplateParam;
import com.medusa.gruul.addon.freight.model.vo.LogisticsTemplateVO;
import com.medusa.gruul.addon.freight.service.LogisticsTemplateInfoService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.order.api.addon.freight.PlatformFreightParam;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 物流模板信息控制层
 *
 * @author xiaoq
 * @Description 物流模板信息控制层
 * @date 2022-05-27 10:16
 */

@RestController
@RequiredArgsConstructor
@PreAuthorize("""
                @S.matcher()
                .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN)
                .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'goods:list','freight:logistics'))
                .match()
        """)
@RequestMapping("logistics/template")
public class LogisticsTemplateController {

    private final LogisticsTemplateInfoService logisticsTemplateInfoService;


    /**
     * 获取模板列表
     *
     * @return Result<IPage < LogisticsTemplateVO>>
     */
    @GetMapping(value = "/get/list")
    public Result<IPage<LogisticsTemplateVO>> getTemplateList(LogisticsTemplateParam logisticsTemplateParam) {
        return Result.ok(logisticsTemplateInfoService.getTemplateList(logisticsTemplateParam));
    }

    /**
     * 获取单个模板详情
     *
     * @param id 模板id
     * @return Result<LogisticsTemplateVO>
     */
    @GetMapping(value = "/get/info")
    @PreAuthorize("permitAll()")
    public Result<LogisticsTemplateVO> getTemplateInfo(Long id) {
        return Result.ok(logisticsTemplateInfoService.getTemplateInfo(id));
    }


    /**
     * 删除模板
     *
     * @param id 模板id
     * @return Result.ok()
     */
    @PreAuthorize("permitAll()")
    @DeleteMapping(value = "/delete/info/{id}")
    public Result<Void> removeTemplateInfo(@PathVariable("id") Long id) {
        logisticsTemplateInfoService.removeTemplateInfo(id);
        return Result.ok();
    }


    /**
     * 模板信息保存
     *
     * @return Result.ok()
     */
    @PostMapping(value = "save/info")
    public Result<Void> saveTemplateInfo(@RequestBody LogisticsTemplateDTO logisticsTemplateDto) {
        logisticsTemplateInfoService.saveTemplateInfo(logisticsTemplateDto);
        return Result.ok();
    }


    /**
     * 模板信息修改
     *
     * @return Void
     */
    @PostMapping(value = "update/info")
    public Result<Void> updateTemplateInfo(@RequestBody LogisticsTemplateDTO logisticsTemplateDto) {
        logisticsTemplateInfoService.updateTemplateInfo(logisticsTemplateDto);
        return Result.ok();
    }

    /**
     * 运费计算
     *
     * @param platformFreight 运费计算
     * @return Map<shopId + " : " + logisticsId, 运费金额 ( 当前运费模板 )>
     */
    @Log("运费计算")
    @PreAuthorize("permitAll()")
    @PostMapping("freightCalculation")
    public Result<Map<String, BigDecimal>> freightCalculation(@RequestBody PlatformFreightParam platformFreight) {
        Map<String, BigDecimal> freightMap = logisticsTemplateInfoService.freightCalculation(platformFreight);
        return Result.ok(freightMap);
    }

}
