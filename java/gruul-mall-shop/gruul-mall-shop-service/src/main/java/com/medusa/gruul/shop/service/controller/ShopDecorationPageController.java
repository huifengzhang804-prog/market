package com.medusa.gruul.shop.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.model.dto.DecorationCopyPageParamDTO;
import com.medusa.gruul.shop.service.model.dto.*;
import com.medusa.gruul.shop.service.model.vo.ShopDecorationPageDetailsVO;
import com.medusa.gruul.shop.service.mp.service.IShopDecorationPageService;
import com.medusa.gruul.shop.service.mp.service.IShopDecorationTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 商家端装修页面REST接口
 *
 * @author An.Yan
 *
 */
@RestController
@RequestMapping("/decoration-pages")
@RequiredArgsConstructor
public class ShopDecorationPageController {

    private final IShopDecorationPageService shopDecorationPageService;
    private final IShopDecorationTemplateService shopDecorationTemplateService;

    /**
     * 分页查询店铺装修页面数据
     *
     * @param dto 分页查询对象,参考{@link ShopDecorationPagePagingQueryDTO}
     * @return {@link ShopDecorationPageDetailsVO}
     */
    @GetMapping
    public Result<IPage<ShopDecorationPageDetailsVO>> pageQuery(ShopDecorationPagePagingQueryDTO dto) {
        return Result.ok(shopDecorationPageService.pageQuery(dto));
    }

    /**
     * 根据ID获取店铺装修页面数据
     *
     * @param id 店铺装修页面ID
     * @return {@link ShopDecorationPageDetailsVO}
     */
    @GetMapping("/{id}")
    public Result<ShopDecorationPageDetailsVO> getShopPageById(@PathVariable Long id) {
        return Result.ok(shopDecorationPageService.getShopPageById(id));
    }


    /**
     * 获取店铺启用的模板所引用的页面对象
     *
     * @param shopId       店铺ID
     * @param endpointType 业务类型
     * @param pageType     页面类型
     * @return {@link ShopDecorationPageDetailsVO}
     */
    @GetMapping("/opening-up/{shopId}/{endpointType}/{pageType}")
    public Result<ShopDecorationPageDetailsVO> getShopPageByEnabledTemplate(@PathVariable Long shopId,
                                                                            @PathVariable DecorationEndpointTypeEnum endpointType,
                                                                            @PathVariable PageTypeEnum pageType,
                                                                            @RequestParam(required = false) String customType
    ) {
        return Result.ok(
                shopDecorationTemplateService.getShopPageByEnabledTemplate(shopId, endpointType, pageType, customType)
        );
    }

    /**
     * 新增店铺装修页面
     *
     * @param dto 店铺装修页面Create DTO {@link ShopDecorationPageCreateDTO}
     * @return {@link Void}
     */
    @PostMapping
    public Result<Long> create(@RequestBody @Valid ShopDecorationPageCreateDTO dto) {
        return Result.ok(shopDecorationPageService.create(dto));
    }

    /**
     * 删除店铺装修页面
     *
     * @param dto 店铺装修页面Delete DTO {@link ShopDecorationPageDeleteDTO}
     * @return {@link Void}
     */
    @DeleteMapping
    public Result<Void> delete(@RequestBody @Valid ShopDecorationPageDeleteDTO dto) {
        shopDecorationPageService.delete(dto);
        return Result.ok();
    }

    /**
     * 修改店铺装修页面
     *
     * @param dto 店铺装修页面Modify DTO {@link ShopDecorationPageModifyDTO}
     * @return {@link Void}
     */
    @PutMapping
    public Result<Void> modify(@RequestBody @Valid ShopDecorationPageModifyDTO dto) {
        shopDecorationPageService.modify(dto);
        return Result.ok();
    }

    /**
     * 克隆店铺装修页面
     *
     * @param dto 店铺装修页面Clone DTO {@link ShopDecorationPageCloneDTO}
     * @return {@link Void}
     */
    @PostMapping("/clone")
    public Result<Void> clone(@RequestBody ShopDecorationPageCloneDTO dto) {
        shopDecorationPageService.clone(dto);
        return Result.ok();
    }

    /**
     * 克隆平台页面
     */
    @PostMapping("/clone/platform")
    public Result<Void> clonePlatformPage(@RequestBody @Valid DecorationCopyPageParamDTO param) {
        shopDecorationPageService.clonePlatformPage(param);
        return Result.ok();
    }
}
