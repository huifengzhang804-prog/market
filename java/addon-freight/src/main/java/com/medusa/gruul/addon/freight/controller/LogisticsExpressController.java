package com.medusa.gruul.addon.freight.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.freight.model.dto.LogisticsExpressDTO;
import com.medusa.gruul.addon.freight.model.param.LogisticsExpressParam;
import com.medusa.gruul.addon.freight.model.vo.LogisticsExpressUsableVO;
import com.medusa.gruul.addon.freight.model.vo.LogisticsExpressVO;
import com.medusa.gruul.addon.freight.service.LogisticsExpressInfoService;
import com.medusa.gruul.addon.freight.service.LogisticsExpressPrintService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.order.api.addon.freight.SendDeliveryDTO;
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
 * 物流公司服务控制层
 *
 * @author xiaoq
 * @Description 物流公司服务控制层
 * @date 2022-06-14 15:57
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/logistics/express")
@PreAuthorize("""
                @S.matcher()
                .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN,@S.R.SUPER_CUSTOM_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN)
                .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'goods:list','freight:logistics'))
                .match()
        """)
public class LogisticsExpressController {

    private final LogisticsExpressPrintService logisticsExpressPrintService;

    private final LogisticsExpressInfoService logisticsExpressInfoService;

    /**
     * 电子面单打印并发货
     *
     * @return Result.ok();
     */
    @Idem(1500)
    @PostMapping(value = "/print/deliver/goods")
    public Result<Void> printDeliverGoods(@RequestBody SendDeliveryDTO sendDeliveryDTO) {
        logisticsExpressPrintService.printDeliverGoods(sendDeliveryDTO);
        return Result.ok();
    }

    /**
     * 物流服务新增
     *
     * @param logisticsExpressDTO 物流服务DTO
     * @return Result.ok();
     */
    @Idem(1000)
    @PostMapping(value = "save")
    public Result<Void> addLogisticsExpress(@RequestBody LogisticsExpressDTO logisticsExpressDTO) {
        logisticsExpressInfoService.addLogisticsExpress(logisticsExpressDTO);
        return Result.ok();
    }

    /**
     * 物流服务修改
     *
     * @param logisticsExpressDTO 物流服务DTO
     * @return Result.ok();
     */
    @Idem(1000)
    @PostMapping(value = "update")
    public Result<Void> updateLogisticsExpress(@RequestBody LogisticsExpressDTO logisticsExpressDTO) {
        logisticsExpressInfoService.updateLogisticsExpress(logisticsExpressDTO);
        return Result.ok();
    }

    /**
     * 批量删除 物流服务
     *
     * @param ids 物流服务ids
     * @return Result.ok();
     */
    @Idem(500)
    @DeleteMapping("del/{ids}")
    public Result<Void> delLogisticsExpress(@PathVariable(name = "ids") Long[] ids) {
        logisticsExpressInfoService.delLogisticsExpress(ids);
        return Result.ok();
    }

    /**
     * 物流服务列表
     *
     * @param logisticsExpressParam param
     * @return Result<IPage < LogisticsExpressVO>>
     */
    @GetMapping("page")
    public Result<IPage<LogisticsExpressVO>> getLogisticsExpressList(LogisticsExpressParam logisticsExpressParam) {
        return Result.ok(logisticsExpressInfoService.getLogisticsExpressList(logisticsExpressParam));
    }

    /**
     * 可用的物流服务列表
     *
     * @param logisticsExpressParam param
     * @return IPage<可用的物流服务VO>
     */
    @PreAuthorize("permitAll()")
    @GetMapping("usable/list")
    public Result<IPage<LogisticsExpressUsableVO>> getLogisticsExpressUsableList(
            LogisticsExpressParam logisticsExpressParam) {
        return Result.ok(logisticsExpressInfoService.getLogisticsExpressUsableList(logisticsExpressParam));
    }

}
