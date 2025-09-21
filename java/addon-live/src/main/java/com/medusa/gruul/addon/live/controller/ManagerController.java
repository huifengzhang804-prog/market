package com.medusa.gruul.addon.live.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.live.enums.ProhibitedType;
import com.medusa.gruul.addon.live.model.AddAnchorDTO;
import com.medusa.gruul.addon.live.model.UpdateAnchorDTO;
import com.medusa.gruul.addon.live.mp.entity.Prohibited;
import com.medusa.gruul.addon.live.param.AnchorParam;
import com.medusa.gruul.addon.live.param.LiveRoomParam;
import com.medusa.gruul.addon.live.service.AddonLiveRoomService;
import com.medusa.gruul.addon.live.service.LiveUserService;
import com.medusa.gruul.addon.live.vo.AnchorVO;
import com.medusa.gruul.addon.live.vo.LiveRoomVO;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 管理端控制器
 *
 * @author miskw
 * @date 2023/6/7
 * @describe 管理端控制器
 */
@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {
    private final AddonLiveRoomService liveRoomService;
    private final LiveUserService liveUserService;

    /**
     * 管理端直播间列表
     *
     * @param liveRoomParam 直播间查询参数
     */
    @Log("管理端直播间列表")
    @GetMapping("/live/list")
    @PreAuthorize("""
                 @S.matcher()
                 .any(@S.ROLES, @S.PLATFORM_ADMIN, @S.SHOP_ADMIN)
                 .or(@S.consumer().eq(@S.ROLES, @S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS, 'marketingApp:liveApp'))
                 .or(@S.consumer().eq(@S.ROLES, @S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS, 'marketingApp:liveApp'))
                 .match()
            """)
    public Result<IPage<LiveRoomVO>> livePage(LiveRoomParam liveRoomParam) {
        Long shopId = ISecurity.userMust().getShopId();
        liveRoomParam.setShopId(shopId);
        return Result.ok(liveRoomService.livePage(liveRoomParam));
    }

    /**
     * 管理端主播列表
     *
     * @param anchorParam 主播列表查询参数
     * @return 分页主播
     */
    @Log("管理端主播列表")
    @GetMapping("/anchor/list")
    @PreAuthorize("""
                 @S.matcher()
                 .any(@S.ROLES, @S.PLATFORM_ADMIN, @S.SHOP_ADMIN)
                 .or(@S.consumer().eq(@S.ROLES, @S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS, 'marketingApp:liveApp'))
                 .or(@S.consumer().eq(@S.ROLES, @S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS, 'marketingApp:liveApp'))
                 .match()
            """)
    public Result<IPage<AnchorVO>> anchorPage(AnchorParam anchorParam) {
        Long shopId = ISecurity.userMust().getShopId();
        anchorParam.setShopId(shopId);
        return Result.ok(liveUserService.anchorPage(anchorParam));
    }

    /**
     * 启用/禁用主播
     *
     * @param id       主播id
     * @param isEnable 启用 | 禁用
     */
    @Log("商家启用/禁用主播")
    @PutMapping("/update/anchor/{id}/{isEnable}")
    @PreAuthorize("""
            @S.matcher()
            .eq(@S.ROLES,@S.SHOP_ADMIN)
            .or(@S.consumer().eq(@S.ROLES, @S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS, 'marketingApp:liveApp'))
            .match()
            """)
    public Result<Void> updateAnchorStatus(@PathVariable("id") Long id, @PathVariable("isEnable") Boolean isEnable) {
        liveUserService.updateAnchorStatus(id, isEnable);
        return Result.ok();
    }

    /**
     * 平台 恢复/违规禁播主播
     *
     * @param updateAnchorDTO 恢复/违规主播参数
     */
    @Log("平台恢复/违规主播")
    @PutMapping("/platform/update/anchor")
    @PreAuthorize("""
            @S.matcher()
            .eq(@S.ROLES, @S.PLATFORM_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS, 'marketingApp:liveApp'))
            .match()
                        """)
    public Result<Void> updateAnchorStatusForPlatform(@Valid @RequestBody UpdateAnchorDTO updateAnchorDTO) {
        liveUserService.updateAnchorStatusForPlatform(updateAnchorDTO);
        return Result.ok();
    }

    /**
     * 查看违禁原因
     *
     * @param id   来源id
     * @param type 来源类型 直播 | 主播
     * @return 违禁原因
     */
    @Log("查看违禁原因")
    @GetMapping("/ban/reason/{id}/{type}")
    @PreAuthorize("""
                 @S.matcher()
                 .any(@S.ROLES, @S.PLATFORM_ADMIN, @S.SHOP_ADMIN)
                 .or(@S.consumer().eq(@S.ROLES, @S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS, 'marketingApp:liveApp'))
                 .or(@S.consumer().eq(@S.ROLES, @S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS, 'marketingApp:liveApp'))
                 .match()
            """)
    public Result<Prohibited> banReason(@PathVariable Long id, @PathVariable ProhibitedType type) {
        return Result.ok(liveUserService.banReason(id, type));
    }

    /**
     * 添加主播
     *
     * @param addAnchorDTO 添加主播所需参数
     */
    @Log("添加主播")
    @PostMapping("/add/anchor")
    @PreAuthorize("""
            @S.matcher()
            .eq(@S.ROLES,@S.SHOP_ADMIN)
            .or(@S.consumer().eq(@S.ROLES, @S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS, 'marketingApp:liveApp'))
            .match()
            """)
    public Result<Void> addAnchor(@Valid @RequestBody AddAnchorDTO addAnchorDTO) {
        liveUserService.addAnchor(addAnchorDTO);
        return Result.ok();
    }
}
