package com.medusa.gruul.order.service.modules.printer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.web.parameter.enums.BodyParam;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrinterEditDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrinterPageDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrinterSaveDTO;
import com.medusa.gruul.order.service.modules.printer.model.vo.PrinterRecordVO;
import com.medusa.gruul.order.service.modules.printer.service.PrinterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 打印机控制器
 *
 * @author 张治保
 * @since 2024/8/21
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/order/printer")
@PreAuthorize("@SS.shop('intra:city','shop:store').match()")
public class PrinterController {

    private final PrinterService printerService;

    /**
     * 添加打印机
     *
     * @param param 打印机参数
     * @return void
     */
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid PrinterSaveDTO param) {
        printerService.save(ISecurity.userMust().getShopId(), param);
        return Result.ok();
    }

    /**
     * 编辑打印机
     *
     * @param param 打印机编辑参数
     * @return void
     */
    @PostMapping("/edit")
    public Result<Void> edit(@RequestBody @Valid PrinterEditDTO param) {
        printerService.edit(ISecurity.userMust().getShopId(), param);
        return Result.ok();
    }


    /**
     * 打印机分页查询
     *
     * @param param 分页查询参数
     * @return 分页查询结果
     */
    @PostMapping("/page")
    public Result<IPage<PrinterRecordVO>> page(@RequestBody @Valid PrinterPageDTO param) {
        return Result.ok(
                printerService.page(ISecurity.userMust().getShopId(), param)
        );
    }

    /**
     * 删除打印机 已绑定打印任务的打印机不可删除
     *
     * @param printerId 打印机 id
     * @return void
     */
    @PostMapping("/delete")
    public Result<Void> delete(@BodyParam Long printerId) {
        printerService.delete(ISecurity.userMust().getShopId(), printerId);
        return Result.ok();
    }

}
