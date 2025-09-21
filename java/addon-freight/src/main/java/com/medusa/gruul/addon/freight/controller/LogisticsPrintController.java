package com.medusa.gruul.addon.freight.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.freight.model.dto.LogisticsPrintDTO;
import com.medusa.gruul.addon.freight.model.param.LogisticsPrintParam;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsPrint;
import com.medusa.gruul.addon.freight.service.LogisticsExpressPrintService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.model.resp.Result;
import jakarta.validation.Valid;
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
 * 物流打印机控制层
 *
 * @author xiaoq
 * @Description
 * @date 2022-08-11 16:00
 */
@RestController
@RequestMapping("logistics/print")
@PreAuthorize("""
                @S.matcher()
                .any(@S.ROLES,@S.SHOP_ADMIN,@S.PLATFORM_ADMIN,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN)
                .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'goods:list','freight:logistics'))
                .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).any(@S.PERMS,'platform:delivery'))
                .match()
        """)
@RequiredArgsConstructor
public class LogisticsPrintController {

    private final LogisticsExpressPrintService logisticsExpressPrintService;

    /**
     * 物流打印机新增
     *
     * @param logisticsPrint 物流打印dto
     * @return Result.ok()
     */
    @Idem(500)
    @PostMapping("/save")
    public Result<Void> addPrintDevice(@RequestBody @Valid LogisticsPrintDTO logisticsPrint) {
        logisticsExpressPrintService.addPrintDevice(logisticsPrint);
        return Result.ok();
    }

    /**
     * 物流打印机修改
     *
     * @param logisticsPrintDTO logisticsPrintDTO
     * @return logisticsPrintDTO
     */
    @Idem(500)
    @PostMapping("/update")
    public Result<Void> updatePrintDevice(@RequestBody @Valid LogisticsPrintDTO logisticsPrintDTO) {
        logisticsExpressPrintService.updatePrintDevice(logisticsPrintDTO);
        return Result.ok();

    }

    /**
     * 物流打印机列表
     *
     * @param logisticsPrintParam 分页
     * @return IPage<LogisticsPrint>
     */
    @GetMapping("list")
    public Result<IPage<LogisticsPrint>> getPrintDeviceList(LogisticsPrintParam logisticsPrintParam) {
        return Result.ok(logisticsExpressPrintService.getPrintDeviceList(logisticsPrintParam));
    }

    /**
     * 物流打印删除
     *
     * @param ids 打印机ids
     * @return Result.ok()
     */
    @DeleteMapping("del/{ids}")
    public Result<Void> delPrintDevice(@PathVariable(name = "ids") Long[] ids) {
        logisticsExpressPrintService.delPrintDevice(ids);
        return Result.ok();
    }
}
