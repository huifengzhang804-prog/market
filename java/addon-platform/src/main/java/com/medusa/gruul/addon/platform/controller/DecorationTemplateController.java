package com.medusa.gruul.addon.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.platform.model.dto.*;
import com.medusa.gruul.addon.platform.model.enums.TemplateTypeEnum;
import com.medusa.gruul.addon.platform.model.vo.DecorationTemplatePagesVO;
import com.medusa.gruul.addon.platform.model.vo.DecorationTemplateVO;
import com.medusa.gruul.addon.platform.mp.service.IDecorationTemplateService;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <p>平台装修模板控制器</p>
 *
 * @author Andy.Yan
 */
@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
@PreAuthorize("@S.platformPerm('decoration')")
@Qualifier("platformDecorationTemplateController")
public class DecorationTemplateController {

    private final IDecorationTemplateService decorationTemplateService;


    /**
     * 创建平台装修模板
     *
     * @param dto 平台装修模板DTO,参考{@link DecorationTemplateCreateDTO}
     * @return {@link Void}
     */
    @PostMapping
    public Result<Void> create(@RequestBody @Valid DecorationTemplateCreateDTO dto) {
        decorationTemplateService.create(dto);
        return Result.ok();
    }

    /**
     * 分页获取平台装修模板
     *
     * @param pageQueryDTO 分页参数,参考{@link DecorationTemplatePageQueryDTO}
     * @return {@link DecorationTemplateVO}
     */
    @GetMapping
    @PreAuthorize("@S.anyPerm('decoration','decoration')")
    public Result<IPage<DecorationTemplateVO>> pageQuery(DecorationTemplatePageQueryDTO pageQueryDTO) {
        return Result.ok(decorationTemplateService.pageQuery(pageQueryDTO));
    }

    /**
     * 删除模板
     *
     * @param id 模板ID
     * @return {@link Void}
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        decorationTemplateService.delete(id);
        return Result.ok();
    }

    /**
     * 复制模板
     *
     * @param dto 源模板,参考 {@link DecorationTemplateCloneDTO}
     */
    @PostMapping("/clone")
    public Result<Void> clone(@RequestBody DecorationTemplateCloneDTO dto) {
        decorationTemplateService.clone(dto);
        return Result.ok();
    }

    /**
     * 编辑模板
     *
     * @param dto 要编辑的模板对象,参考{@link DecorationTemplateModifyDTO}
     * @return {@link Void}
     */
    @PutMapping
    public Result<Void> modifyTemplate(@RequestBody @Valid DecorationTemplateModifyDTO dto) {
        decorationTemplateService.modify(dto);
        return Result.ok();
    }

    /**
     * 获取模板详情
     *
     * @param id 模板ID
     * @return 模板信息, 参考 {@link DecorationTemplateVO}
     */
    @GetMapping("/{id}")
    public Result<DecorationTemplateVO> getDecorationTemplate(@PathVariable("id") Long id) {
        return Result.ok(decorationTemplateService.getDecorationTemplate(id));
    }

    /**
     * 获取平台端启用的模版对象
     *
     * @return 模板信息, 参考 {@link DecorationTemplateVO}
     */
    @GetMapping("/opening-up")
    public Result<DecorationTemplateVO> getOpeningUpDecorationTemplate() {
        return Result.ok(decorationTemplateService.getOpeningUpDecorationTemplate());
    }

    /**
     * 修改模板状态
     *
     * @param statusChangeDTO {@link DecorationTemplateStatusChangeDTO}
     */
    @PutMapping("/default-template")
    public Result<Void> modifyTemplateStatus(@RequestBody @Valid DecorationTemplateStatusChangeDTO statusChangeDTO) {
        decorationTemplateService.modifyTemplateStatus(statusChangeDTO);
        return Result.ok();
    }

    /**
     * 按照页面类型获取页面集合
     *
     * @return {@link DecorationTemplatePagesVO}
     */
    @GetMapping("/pages")
    @PreAuthorize("permitAll()")
    public Result<DecorationTemplatePagesVO> listPages(
            @RequestParam(value = "templateType") TemplateTypeEnum templateType,
            @RequestParam(value = "businessType", required = false) TemplateBusinessTypeEnum businessType,
            @RequestParam(value = "endpointType") DecorationEndpointTypeEnum endpointType) {
        return Result.ok(decorationTemplateService.listPages(templateType, businessType, endpointType));
    }
}
