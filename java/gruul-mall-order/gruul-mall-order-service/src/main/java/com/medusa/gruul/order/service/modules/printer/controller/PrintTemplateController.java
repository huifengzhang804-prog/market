package com.medusa.gruul.order.service.modules.printer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.web.parameter.enums.BodyParam;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrintTemplateDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrintTemplatePageDTO;
import com.medusa.gruul.order.service.modules.printer.model.vo.PrintTemplateRecordVO;
import com.medusa.gruul.order.service.modules.printer.mp.entity.PrintTemplate;
import com.medusa.gruul.order.service.modules.printer.service.PrintTemplateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 打印模板控制器
 *
 * @author 张治保
 * @since 2024/8/21
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/order/print/template")
@PreAuthorize("@SS.shop('intra:city','shop:store').match()")
public class PrintTemplateController {

    private final PrintTemplateService printTemplateService;

    /**
     * 新增或编辑打印模板
     *
     * @param param 打印模板参数
     * @return 执行结果
     */
    @PostMapping
    public Result<Void> saveOrUpdate(@RequestBody @Valid PrintTemplateDTO param) {
        printTemplateService.saveOrUpdate(ISecurity.userMust().getShopId(), param);
        return Result.ok();
    }

    /**
     * 分页查询打印模板
     *
     * @param param 分页查询参数
     * @return 分页查询结果
     */
    @PostMapping("/page")
    public Result<IPage<PrintTemplateRecordVO>> page(@RequestBody @Valid PrintTemplatePageDTO param) {
        return Result.ok(
                printTemplateService.page(ISecurity.userMust().getShopId(), param)
        );
    }

    /**
     * 根据 模板id查询模板详情
     *
     * @param id 模板 id
     * @return 模板详情
     */
    @PostMapping("/detail")
    public Result<PrintTemplate> detail(@BodyParam @NotNull Long id) {
        return Result.ok(
                printTemplateService.detail(ISecurity.userMust().getShopId(), id)
        );
    }

    /**
     * 根据模板 id删除打印模板
     *
     * @param id 模板 id
     * @return 删除结果
     */
    @PostMapping("/delete")
    public Result<Void> delete(@BodyParam @NotNull Long id) {
        printTemplateService.delete(ISecurity.userMust().getShopId(), id);
        return Result.ok();
    }

}
