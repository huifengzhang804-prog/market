package com.medusa.gruul.addon.full.reduction.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.full.reduction.model.dto.FullReductionDTO;
import com.medusa.gruul.addon.full.reduction.model.dto.FullReductionPageDTO;
import com.medusa.gruul.addon.full.reduction.model.dto.FullReductionShelfDTO;
import com.medusa.gruul.addon.full.reduction.model.vo.FullReductionDetailVO;
import com.medusa.gruul.addon.full.reduction.model.vo.FullReductionVO;
import com.medusa.gruul.addon.full.reduction.service.FullReductionService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.CurrentActivityKey;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.web.parameter.enums.BodyParam;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 商家平台端满减活动 前端控制器
 *
 * @author WuDi
 * @since 2023-02-07
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/full/reduction")
public class FullReductionController {

    private final FullReductionService fullReductionService;

    /**
     * 编辑满减活动
     *
     * @param param 满减活动参数
     */
    @Log("新增、编辑满减活动")
    @PostMapping
    @PreAuthorize("@S.shopPerm('marketingApp:applyDiscount')")
    public Result<Void> saveOrUpdate(@RequestBody @Validated FullReductionDTO param) {
        param.validParam();
        fullReductionService.saveOrUpdate(ISecurity.userMust().getShopId(), param);
        return Result.ok();
    }

    /**
     * 分页查询满减活动
     *
     * @param param 分页查询参数
     * @return 满减活动分页结果
     */
    @Log("分页查询满减活动")
    @PostMapping("/page")
    @PreAuthorize("@S.anyPerm('applyDiscount','marketingApp:applyDiscount')")
    public Result<IPage<FullReductionVO>> page(@RequestBody FullReductionPageDTO param) {
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> param.setShopId(secureUser.getShopId()));
        return Result.ok(
                fullReductionService.page(param)
        );
    }


    /**
     * 查询单个满减活动
     *
     * @param shopId 店铺 id 店铺可以为空 平台必填
     * @param id     满减活动 id
     * @return 满减活动
     */
    @Log("查询单个满减活动")
    @PostMapping("/detail")
    @PreAuthorize("@S.anyPerm('applyDiscount','marketingApp:applyDiscount')")
    public Result<FullReductionDetailVO> detail(@BodyParam(required = false) Long shopId, @BodyParam Long id) {
        CurrentActivityKey key = new CurrentActivityKey()
                .setShopId(shopId)
                .setActivityId(id);
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> key.setShopId(secureUser.getShopId()));
        if (key.getShopId() == null) {
            throw SystemCode.PARAM_VALID_ERROR.exception("shopId cannot be null");
        }
        return Result.ok(
                fullReductionService.detail(key)
        );
    }

    /**
     * 店铺端 批量删除满减活动
     *
     * @param activityIds 活动 id 集合
     * @return void
     */
    @Log("店铺端批量删除满减活动")
    @DeleteMapping("/batch/delete")
    @PreAuthorize("@S.shopPerm('applyDiscount')")
    public Result<Void> batchDelete(@RequestBody @Validated @Size(min = 1) Set<Long> activityIds) {
        fullReductionService.batchDelete(ISecurity.userMust().getShopId(), activityIds);
        return Result.ok();
    }

    /**
     * 平台批量下架满减活动 违规下架
     *
     * @param param 满减活动下架参数
     */
    @Log("活动下架")
    @PostMapping("/shelf")
    @PreAuthorize("@S.anyPerm('applyDiscount','marketingApp:applyDiscount')")
    public Result<Void> shelf(@RequestBody @Validated FullReductionShelfDTO param) {
        boolean isShop = ISecurity.matcher().anyRole(Roles.ADMIN, Roles.CUSTOM_ADMIN).match();
        param.validParam(isShop);
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> param.setShopId(secureUser.getShopId()));
        fullReductionService.shelf(isShop, param);
        return Result.ok();
    }


    /**
     * 获取店铺的满减规则标签列表
     *
     * @param shopIds 店铺id集合
     * @return 店铺的满减规则标签列表 key为店铺id value为满减规则标签列表
     */
    @PostMapping("/shop/rule/labels")
    public Result<Map<Long, List<String>>> shopRuleLabels(@RequestBody @Size(min = 1) Set<Long> shopIds) {
        return Result.ok(
                fullReductionService.shopRuleLabels(shopIds)
        );
    }


}
