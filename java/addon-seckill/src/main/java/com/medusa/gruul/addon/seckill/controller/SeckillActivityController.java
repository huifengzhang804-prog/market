package com.medusa.gruul.addon.seckill.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.seckill.model.dto.*;
import com.medusa.gruul.addon.seckill.model.vo.*;
import com.medusa.gruul.addon.seckill.service.SeckillActivityService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.web.parameter.enums.BodyParam;
import com.medusa.gruul.global.model.o.Final;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/5/28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/seckill/activity")
public class SeckillActivityController {

    private final SeckillActivityService seckillActivityService;

    /**
     * 查询可用的活动场次
     *
     * @param date 活动日期
     * @return 可用的活动场次
     */
    @Log("查询可用的活动场次")
    @PostMapping("/round")
    @PreAuthorize("@S.shopPerm('marketingApp:secondsKill')")
    public Result<List<RoundVO>> rounds(@BodyParam LocalDate date) {
        return Result.ok(
                seckillActivityService.rounds(date)
        );
    }

    /**
     * 新增秒杀活动
     *
     * @param activity 秒杀活动
     * @return void
     */
    @Log("新增秒杀活动")
    @Idem(500)
    @PostMapping
    @PreAuthorize("@S.shopPerm('marketingApp:secondsKill')")
    public Result<Void> create(@RequestBody @Valid SeckillActivityDTO activity) {
        activity.validParam();
        // 创建秒杀活动
        seckillActivityService.create(ISecurity.userMust().getShopId(), activity);
        return Result.ok();
    }

    /**
     * 分页查询秒杀活动
     *
     * @param query 查询条件
     * @return 秒杀活动分页数据
     */
    @Log("分页查询秒杀活动")
    @PostMapping("/page")
    @PreAuthorize("@S.anyPerm('secondsKill','marketingApp:secondsKill')")
    public Result<IPage<SeckillActivityVO>> page(@RequestBody SeckillActivityQueryDTO query) {
        return Result.ok(seckillActivityService.page(query));
    }

    /**
     * 查询秒杀活动详情
     *
     * @param shopId     店铺id
     * @param activityId 活动id
     * @return 秒杀活动详情
     */
    @Log("查询秒杀活动详情")
    @PostMapping("/detail")
    @PreAuthorize("@S.anyPerm('secondsKill','marketingApp:secondsKill')")
    public Result<SeckillActivityDetailVO> detail(@BodyParam(required = false) Long shopId, @BodyParam Long activityId) {
        Final<Long> shopIdFinal = new Final<>(shopId);
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> shopIdFinal.set(secureUser.getShopId()))
                .other(secureUser -> SystemCode.PARAM_VALID_ERROR.trueThrow(shopId == null));
        return Result.ok(seckillActivityService.detail(shopIdFinal.get(), activityId));
    }

    /**
     * 活动下架
     *
     * @param offShelf 下架参数
     */
    @Log("活动下架")
    @PostMapping("/off/shelf")
    @PreAuthorize("@S.anyPerm('secondsKill','marketingApp:secondsKill')")
    public Result<Void> offShelf(@RequestBody @Valid OffShelfDTO offShelf) {
        //是否是店铺自自行下架 否则是平台下架
        boolean isShop = ISecurity.anyRole(Roles.ADMIN, Roles.CUSTOM_ADMIN);
        offShelf.validParam(isShop);
        if (isShop) {
            offShelf.setShopId(ISecurity.userMust().getShopId());
        }
        seckillActivityService.offShelf(isShop, offShelf);
        return Result.ok();
    }

    /**
     * 批量活动删除
     */
    @Log
    @PostMapping("/delete")
    @PreAuthorize("@S.shopPerm('marketingApp:secondsKill')")
    public Result<Void> delete(@RequestBody Set<Long> activityIds) {
        seckillActivityService.delete(ISecurity.userMust().getShopId(), activityIds);
        return Result.ok();
    }


    /**
     * 分页查询秒杀活动场次 按开始时间从小到大排序
     *
     * @param page 分页查询参数
     * @return 分页查询结果
     */
    @Log("分页查询秒杀活动场次")
    @PostMapping("/round/page")
    public Result<IPage<SeckillRoundVO>> roundPage(@RequestBody SeckillRoundPageDTO page) {
        return Result.ok(
                seckillActivityService.roundPage(page)
        );
    }

    /**
     * 分页查询对应场次的商品
     *
     * @param page 分页查询参数
     * @return 分页查询结果
     */
    @Log("分页查询场次商品")
    @PostMapping("/round/product/page")
    public Result<IPage<SeckillRoundProductVO>> roundProductPage(@RequestBody @Valid SeckillRoundProductPageDTO page) {
        return Result.ok(
                seckillActivityService.roundProductPage(page)
        );
    }


}
