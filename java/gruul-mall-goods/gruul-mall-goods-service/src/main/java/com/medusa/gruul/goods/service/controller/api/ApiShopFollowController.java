package com.medusa.gruul.goods.service.controller.api;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.goods.api.model.dto.ShopFollowDTO;
import com.medusa.gruul.goods.api.model.enums.MyShopFollowStatus;
import com.medusa.gruul.goods.api.model.param.ApiShopFollowParam;
import com.medusa.gruul.goods.api.model.vo.ApiFollowShopProductVO;
import com.medusa.gruul.goods.service.model.vo.MyShopFollowVO;
import com.medusa.gruul.goods.service.model.vo.ShopFollowVO;
import com.medusa.gruul.goods.service.mp.service.IShopFollowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺关注 前端控制器
 *
 * @author WuDi
 * @since 2022-08-03
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("@S.matcher().role(@S.R.USER).match()")
@RequestMapping("/shop/follow")
public class ApiShopFollowController {


    private final IShopFollowService shopFollowService;

    /**
     * 关注/取消关注店铺
     *
     * @param shopFollowDTO 浏览商品DTO
     */
    @Log("关注/取消关注店铺")
    @PutMapping
    @Idem
    public Result<Void> follow(@RequestBody @Valid ShopFollowDTO shopFollowDTO) {
        shopFollowService.follow(shopFollowDTO);
        return Result.ok();
    }

    /**
     * 判断用户是否关注当前店铺
     *
     * @param shopId 店铺id
     */
    @Log("判断用户是否关注当前店铺")
    @GetMapping("/or/not/{shopId}")
    public Result<Boolean> isFollow(@PathVariable("shopId") Long shopId) {
        return Result.ok(
                shopFollowService.isFollow(shopId)
        );
    }

    /**
     * 获取关注下的店铺
     *
     * @param shopFollowParam 店铺查询参数
     */
    @Log("获取关注下的店铺")
    @GetMapping("/shopInfo")
    public Result<IPage<ShopFollowVO>> shopInfo(ApiShopFollowParam shopFollowParam) {
        return Result.ok(
                shopFollowService.shopInfo(shopFollowParam)
        );
    }


    /**
     * 我的关注-全部店铺
     *
     * @param shopFollowParam    店铺查询参数
     * @param myShopFollowStatus 我的关注枚举
     */
    @Log("我的关注-全部店铺")
    @GetMapping("/myFollow/{status}")
    public Result<IPage<MyShopFollowVO>> myFollow(ApiShopFollowParam shopFollowParam,
                                                  @PathVariable("status") MyShopFollowStatus myShopFollowStatus) {
        return Result.ok(
                shopFollowService.myFollow(shopFollowParam, myShopFollowStatus)
        );
    }

    /**
     * 首页我的关注全部店铺
     *
     * @param shopFollowParam 店铺查询参数
     */
    @Log("首页我的关注全部店铺")
    @GetMapping("/myFollow")
    public Result<IPage<MyShopFollowVO>> homePageMyFollow(ApiShopFollowParam shopFollowParam) {
        return Result.ok(
                shopFollowService.homePageMyFollow(shopFollowParam)
        );
    }

    /**
     * pc端-关注店铺
     *
     * @param shopFollowParam 店铺查询参数
     */
    @Log("pc端-关注店铺")
    @GetMapping("/followShopProducts")
    public Result<IPage<ApiFollowShopProductVO>> shopFollowProducts(ApiShopFollowParam shopFollowParam) {
        return Result.ok(
                shopFollowService.shopFollowProducts(shopFollowParam)
        );
    }

    /**
     * 获取店铺关注人数
     *
     * @param shopId 店铺id
     * @return 关注人数
     */
    @Log("获取店铺关注人数")
    @GetMapping("/followCount/{shopId}")
    @PreAuthorize("permitAll()")
    public Result<Long> followCount(@PathVariable("shopId") Long shopId) {
        return Result.ok(
                shopFollowService.followCount(shopId)
        );
    }

}

