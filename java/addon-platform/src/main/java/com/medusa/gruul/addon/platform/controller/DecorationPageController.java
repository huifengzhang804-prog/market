package com.medusa.gruul.addon.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.platform.model.dto.DecorationPageCreateDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationPageModifyDTO;
import com.medusa.gruul.addon.platform.model.dto.DecorationPageQueryDTO;
import com.medusa.gruul.addon.platform.model.vo.DecorationPageDetailVO;
import com.medusa.gruul.addon.platform.model.vo.DecorationPageVO;
import com.medusa.gruul.addon.platform.mp.service.IDecorationPageService;
import com.medusa.gruul.addon.platform.mp.service.IDecorationTemplateService;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * <p>平台装修页面Rest接口</p>
 *
 * @author An.Yan
 */
@RestController
@RequestMapping("/pages")
@RequiredArgsConstructor
public class DecorationPageController {

    private final IDecorationPageService decorationPageService;
    private final IDecorationTemplateService decorationTemplateService;

    /**
     * 创建平台页面数据
     *
     * @param dto 平台页面DTO,参考{@link DecorationPageCreateDTO}
     * @return {@link Void}
     */
    @PostMapping
    @PreAuthorize("@S.platformPerm('decoration')")
    public Result<Long> create(@RequestBody @Valid DecorationPageCreateDTO dto) {
        return Result.ok(decorationPageService.create(dto));
    }

    /**
     * 修改平台装修页面数据
     *
     * @param dto 平台页面DTO,参考{@link DecorationPageCreateDTO}
     * @return {@link Void}
     */
    @PutMapping
    @PreAuthorize("@S.platformPerm('decoration')")
    public Result<Void> modify(@RequestBody @Valid DecorationPageModifyDTO dto) {
        decorationPageService.modify(dto);
        return Result.ok();
    }

    /**
     * 分页获取平台装修页面
     *
     * @param dto 分页参数,参考{@link DecorationPageQueryDTO}
     * @return {@link DecorationPageVO}
     */
    @GetMapping
    @PreAuthorize("@S.anyPerm('decoration','decoration')")
    public Result<IPage<DecorationPageVO>> pageQuery(@Valid DecorationPageQueryDTO dto) {
        return Result.ok(decorationPageService.pageQuery(dto));
    }

    /**
     * 删除页面
     *
     * @param id 模板ID
     * @return {@link Void}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@S.platformPerm('decoration')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        decorationPageService.delete(id);
        return Result.ok();
    }

    /**
     * 复制页面
     *
     * @param id 页面ID
     */
    @PostMapping("/clone/{id}")
    @PreAuthorize("@S.platformPerm('decoration')")
    public Result<Void> clone(@PathVariable("id") Long id) {
        decorationPageService.clone(id);
        return Result.ok();
    }

    /**
     * 获取页面详情
     *
     * @param id 页面ID
     * @return {@link DecorationPageDetailVO}
     */
    @GetMapping("/{id}")
    public Result<DecorationPageDetailVO> getPageById(@PathVariable("id") Long id) {
        return Result.ok(decorationPageService.getPageById(id));
    }


    /**
     * 获取平台启用的模版中的页面信息(根据页面类型)
     *
     * @param pageTypeEnum 页面类型枚举
     * @return {@link DecorationPageDetailVO}
     */
    @GetMapping("/opening-up/{endpointType}/{pageType}")
    public Result<DecorationPageDetailVO> getPageByEnabledTemplate(
            @PathVariable("endpointType") DecorationEndpointTypeEnum endpointType,
            @PathVariable("pageType") PageTypeEnum pageTypeEnum,
            @RequestParam(required = false) String customType
    ) {
        return Result.ok(decorationTemplateService.getPageByEnabledTemplate(endpointType, pageTypeEnum, customType));
    }
}
