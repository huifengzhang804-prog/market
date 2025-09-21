package com.medusa.gruul.shop.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import com.medusa.gruul.shop.api.model.dto.DecorationCopyTemplateParamDTO;
import com.medusa.gruul.shop.service.model.dto.*;
import com.medusa.gruul.shop.service.model.vo.DecorationTemplatePagesVO;
import com.medusa.gruul.shop.service.model.vo.ShopDecorationTemplateDetailsVO;
import com.medusa.gruul.shop.service.model.vo.ShopDecorationTemplatePageVO;
import com.medusa.gruul.shop.service.mp.service.IShopDecorationTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺装修模板Rest接口
 * @author Andy.Yan
 */
@RestController
@RequestMapping("/decoration-templates")
@RequiredArgsConstructor
public class ShopDecorationTemplateController {

    private final IShopDecorationTemplateService shopDecorationTemplateService;

    /**
     * 创建店铺模板
     *
     * @param dto 模板对象,参考{@link ShopDecorationTemplateCreateDTO}
     */
    @PostMapping
    public Result<Void> create(@RequestBody @Valid ShopDecorationTemplateCreateDTO dto) {
        shopDecorationTemplateService.createTemplate(dto);
        return Result.ok();
    }

    /**
     * 删除店铺模板
     *
     * @param dto 模板对象,参考{@link ShopDecorationTemplateDeleteDTO}
     */
    @DeleteMapping
    public Result<Void> delete(@RequestBody @Valid ShopDecorationTemplateDeleteDTO dto) {
        shopDecorationTemplateService.delete(dto);
        return Result.ok();
    }

    /**
     * 修改店铺模板
     *
     * @param dto 模板对象,参考{@link ShopDecorationTemplateModifyDTO}
     */
    @PutMapping
    public Result<Void> modify(@RequestBody @Valid ShopDecorationTemplateModifyDTO dto) {
        shopDecorationTemplateService.modify(dto);
        return Result.ok();
    }

    /**
     * 根据ID获取店铺模板详情
     *
     * @param id 模板ID
     */
    @GetMapping("/{id}")
    public Result<ShopDecorationTemplateDetailsVO> getTemplateById(@PathVariable("id") Long id) {
        return Result.ok(shopDecorationTemplateService.getShopDecorationTemplateById(id));
    }

    /**
     * 获取店铺启用的模板对象
     *
     * @return {@link ShopDecorationTemplateDetailsVO}
     */
    @GetMapping("/opening-up")
    public Result<ShopDecorationTemplateDetailsVO> getOpeningUpTemplate() {
        return Result.ok(shopDecorationTemplateService.getOpeningUpTemplate());
    }

    /**
     * 分页查询店铺模板列表
     *
     * @param param 分页查询对象,参考{@link ShopDecorationPageQueryDTO}
     */
    @GetMapping
    public Result<IPage<ShopDecorationTemplatePageVO>> pageQuery(@Valid ShopDecorationPageQueryDTO param) {
        return Result.ok(shopDecorationTemplateService.pageQuery(param));
    }

    /**
     * 复制模板
     *
     * @param dto 源模板,参考 {@link ShopDecorationTemplateCloneDTO}
     */
    @PostMapping("/clone")
    public Result<Void> clone(@RequestBody ShopDecorationTemplateCloneDTO dto) {
        shopDecorationTemplateService.clone(dto);
        return Result.ok();
    }

    /**
     * 复制平台模板
     */
    @PostMapping("/clone/platform")
    public Result<Void> clonePlatformTemplate(@RequestBody @Valid DecorationCopyTemplateParamDTO param) {
        shopDecorationTemplateService.copyPlatformTemplate(ISecurity.userMust().getShopId(), param);
        return Result.ok();
    }

    /**
     * 修改模板状态
     *
     * @param statusChangeDTO {@link ShopDecorationTemplateChangeStatusDTO}
     */
    @PutMapping("/default-template")
    public Result<Void> modifyTemplateStatus(@RequestBody ShopDecorationTemplateChangeStatusDTO statusChangeDTO) {
        shopDecorationTemplateService.modifyTemplateStatus(statusChangeDTO);
        return Result.ok();
    }

    /**
     * 根据页面类型获取页面集合
     *
     * @return {@link DecorationTemplatePagesVO}
     */
    @GetMapping("/pages")
    public Result<DecorationTemplatePagesVO> listPages(
            @RequestParam(value = "businessType") TemplateBusinessTypeEnum businessType,
            @RequestParam(value = "endpointType") DecorationEndpointTypeEnum endpointType) {
        return Result.ok(shopDecorationTemplateService.listPages(businessType, endpointType));
    }
}
