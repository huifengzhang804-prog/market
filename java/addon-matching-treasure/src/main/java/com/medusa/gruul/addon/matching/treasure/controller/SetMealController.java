package com.medusa.gruul.addon.matching.treasure.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.matching.treasure.model.dto.SetMealDTO;
import com.medusa.gruul.addon.matching.treasure.model.dto.SetMealQueryDTO;
import com.medusa.gruul.addon.matching.treasure.model.dto.ShopSetMealIdDTO;
import com.medusa.gruul.addon.matching.treasure.model.vo.SetMealVO;
import com.medusa.gruul.addon.matching.treasure.service.SetMealManageService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐店铺平台端 前端控制器
 *
 * @author WuDi
 * @since 2023-02-27
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/setMeal")
public class SetMealController {

    private final SetMealManageService setMealManageService;


    /**
     * 新增套餐
     *
     * @param setMealDTO 套餐DTO
     */
    @Log("新增套餐")
    @PreAuthorize("@S.shopPerm('bundlePrice:Index')")
    @PostMapping
    public Result<Void> addSetMeal(@RequestBody @Valid SetMealDTO setMealDTO) {
        setMealDTO.validParams();
        Long shopId = ISecurity.userMust().getShopId();
        setMealManageService.addSetMeal(shopId, setMealDTO);
        return Result.ok();
    }


    /**
     * 分页查询套餐活动
     *
     * @param setMealQuery 查询参数
     * @return 分页结果
     */
    @Log("分页查询套餐活动")
    @PreAuthorize("@S.anyPerm('bundlePrice:Index','bundlePrice:Index')")
    @GetMapping("")
    public Result<IPage<SetMealVO>> pageSetMeal(SetMealQueryDTO setMealQuery) {
        return Result.ok(
                setMealManageService.pageSetMeal(setMealQuery)
        );
    }

    /**
     * 查询套餐活动详情
     *
     * @param shopId    店铺id
     * @param setMealId 套餐id
     * @return 套餐详情
     */
    @Log("查询套餐活动详情")
    @GetMapping("/{shopId}/{setMealId}")
    @PreAuthorize("@S.anyPerm('bundlePrice:Index','bundlePrice:Index')")
    public Result<SetMealVO> getSetMealDetailById(@PathVariable Long shopId,
                                                  @PathVariable Long setMealId) {
        return Result.ok(
                setMealManageService.getSetMealDetailById(shopId, setMealId)
        );
    }

    /**
     * 删除套餐活动
     *
     * @param shopId    店铺id
     * @param setMealId 套餐id
     */
    @DeleteMapping("/{shopId}/{setMealId}")
    @PreAuthorize("@S.anyPerm('bundlePrice:Index','bundlePrice:Index')")
    public Result<Void> deleteSetMeal(@PathVariable Long shopId,
                                      @PathVariable Long setMealId) {
        setMealManageService.deleteSetMeal(shopId, setMealId);
        return Result.ok();
    }


    /**
     * 批量删除套餐活动
     *
     * @param shopSetMealIds 店铺、套餐id集合
     */
    @DeleteMapping
    @PreAuthorize("@S.anyPerm('bundlePrice:Index','bundlePrice:Index')")
    public Result<Void> deleteBatchSetMeal(@RequestBody @Valid @Size(min = 1) List<ShopSetMealIdDTO> shopSetMealIds) {
        setMealManageService.deleteBatchSetMeal(shopSetMealIds);
        return Result.ok();
    }

    /**
     * 下架套餐活动
     *
     * @param shopId           店铺id
     * @param setMealId        套餐id
     * @param violationExplain 违规说明
     */
    @PostMapping("/{shopId}/{setMealId}")
    @PreAuthorize("@S.anyPerm('bundlePrice:Index','bundlePrice:Index')")
    public Result<Void> sellOffSetMeal(@PathVariable Long shopId,
                                       @PathVariable Long setMealId,
                                       @Length(max = 50, message = "违规说明超长") String violationExplain) {
        setMealManageService.sellOffSetMeal(shopId, setMealId, violationExplain);
        return Result.ok();
    }
}
