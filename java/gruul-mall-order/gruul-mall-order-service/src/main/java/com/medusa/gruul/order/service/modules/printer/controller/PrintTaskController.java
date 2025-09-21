package com.medusa.gruul.order.service.modules.printer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.web.parameter.enums.BodyParam;
import com.medusa.gruul.order.api.enums.PrintLink;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrintTaskDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrintTaskPageDTO;
import com.medusa.gruul.order.service.modules.printer.model.vo.PrintTaskRecordVO;
import com.medusa.gruul.order.service.modules.printer.service.PrintTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 打印任务控制器
 *
 * @author 张治保
 * @since 2024/8/22
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/order/print/task")
@PreAuthorize("@SS.shop('intra:city','shop:store').match()")
public class PrintTaskController {

    private final PrintTaskService printTaskService;

    /**
     * 新增或编辑打印任务
     *
     * @param param 任务参数据
     * @return void
     */
    @PostMapping
    public Result<Void> saveOrUpdate(@RequestBody @Valid PrintTaskDTO param) {
        printTaskService.saveOrUpdate(ISecurity.userMust().getShopId(), param);
        return Result.ok();
    }

    /**
     * 分页查询打印任务
     *
     * @param param 查询参数
     * @return 分页查询结果
     */
    @PostMapping("/page")
    public Result<IPage<PrintTaskRecordVO>> page(@RequestBody @Valid PrintTaskPageDTO param) {
        return Result.ok(
                printTaskService.page(ISecurity.userMust().getShopId(), param)
        );
    }

    /**
     * 删除打印任务
     *
     * @param id 打印机任务 id
     */
    @PostMapping("/delete")
    public Result<Void> delete(@BodyParam Long id) {
        printTaskService.delete(ISecurity.userMust().getShopId(), id);
        return Result.ok();
    }

    /**
     * 商家手动打印订单
     *
     * @param orderNo 订单号
     * @param link    打印类型 、某联（商家联、后厨联。。）
     * @return void
     */
    @Log("商家手动打印订单")
    @PreAuthorize("@SS.shop('order:delivery').match()")
    @PostMapping("/print")
    public Result<Void> printOrder(@BodyParam String orderNo, @BodyParam PrintLink link) {
        printTaskService.printOrder(ISecurity.userMust().getShopId(), orderNo, link);
        return Result.ok();
    }


}
