package com.medusa.gruul.addon.live.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.live.model.FollowAnchorDTO;
import com.medusa.gruul.addon.live.model.ReservationLiveDTO;
import com.medusa.gruul.addon.live.param.FollowLiveParam;
import com.medusa.gruul.addon.live.service.LiveUserService;
import com.medusa.gruul.addon.live.utils.TextToImageUtil;
import com.medusa.gruul.addon.live.vo.AnchorVO;
import com.medusa.gruul.addon.live.vo.FollowLiveVO;
import com.medusa.gruul.addon.live.vo.LiveRoomVO;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 用户控制器
 *
 * @author miskw
 * @date 2023/6/6
 * @describe 用户控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class LiveUserController {
    private final LiveUserService liveUserService;

    /**
     * 预约直播间
     *
     * @param reservationLiveDTO 预约直播间参数
     */
    @Idem
    @Log("预约直播间")
    @PostMapping("/add/reservation")
    @PreAuthorize(value = "@S.matcher().eq(@S.ROLES,@S.USER).match()")
    public Result<Void> addFollow(@RequestBody @Valid ReservationLiveDTO reservationLiveDTO) {
        liveUserService.addReservation(reservationLiveDTO);
        return Result.ok();
    }

    /**
     * 用户关注主播
     *
     * @param followAnchorDTO 用户关注主播参数
     */
    @Idem
    @Log("用户关注主播")
    @PostMapping("/add/follow")
    @PreAuthorize(value = "@S.matcher().eq(@S.ROLES,@S.USER).match()")
    public Result<Void> addFollow(@RequestBody @Valid FollowAnchorDTO followAnchorDTO) {
        liveUserService.addFollow(followAnchorDTO);
        return Result.ok();
    }

    /**
     * 关注直播间列表
     *
     * @param followLiveParam 参数
     * @return 关注直播间列表
     */
    @Log("关注直播间列表")
    @GetMapping("/follow/live/list")
    @PreAuthorize(value = "@S.matcher().role(@S.USER).match()")
    public Result<IPage<FollowLiveVO>> followLiveList(@Valid FollowLiveParam followLiveParam) {
        Long userId = ISecurity.userMust().getId();
        return Result.ok(liveUserService.followLiveList(followLiveParam, userId));
    }

    /**
     * 用户发现直播间
     *
     * @param followLiveParam 参数
     */
    @Log("用户发现直播间")
    @GetMapping("/discover/live/list")
    public Result<IPage<FollowLiveVO>> discoverLiveList(@Valid FollowLiveParam followLiveParam) {
        Long userId = ISecurity.userMust().getId();
        return Result.ok(liveUserService.discoverLiveList(followLiveParam, userId));
    }

    /**
     * C端获取主播信息
     *
     * @return 主播信息
     */
    @Log("主播信息")
    @GetMapping("/message")
    @PreAuthorize(value = "@S.matcher().eq(@S.SUB_ROLES,@S.ANCHOR).match()")
    public Result<AnchorVO> anchorMessage() {
        return Result.ok(liveUserService.anchorMessage(ISecurity.userMust().getId()));
    }

    /**
     * 用户随机获取一条直播间信息
     *
     * @param id 直播间id
     * @return 直播间信息
     */
    @Log("获取随机一个直播间")
    @GetMapping("/random/view/{id}")
    public Result<LiveRoomVO> randomView(@PathVariable("id") Long id) {
        return Result.ok(liveUserService.randomView(id));
    }

    /**
     * 直播分享图文字转图片
     *
     * @param liveTitle    直播标题
     * @param liveSynopsis 直播简介
     * @return base64图片文字
     */
    @Log("直播分享图文字转图片")
    @GetMapping("/characters")
    public Result<String> characters(@RequestParam("liveTitle") String liveTitle, @RequestParam("liveSynopsis") String liveSynopsis) {
        return Result.ok(TextToImageUtil.textToBase64Image(liveTitle, liveSynopsis));
    }

    /**
     * 用户进入直播间时，添加直播间观看人数
     *
     * @param liveId 直播间id
     */
    @Log("用户进入直播间时，添加直播间观看人数")
    @PostMapping("/viewership/{liveId}")
    public Result<Void> viewership(@PathVariable String liveId) {
        liveUserService.addViewership(liveId);
        return Result.ok();
    }


    /**
     * 用户是否关注 前端需要过滤一遍用户登陆状态
     *
     * @return 是否关注
     */
    @Log("用户是否关注")
    @GetMapping("/viewership/status/{anchorId}")
    @PreAuthorize(value = "@S.matcher().eq(@S.ROLES, @S.USER).match()")
    public Result<Boolean> viewershipStatus(@PathVariable Long anchorId) {
        return Result.ok(liveUserService.viewershipStatus(ISecurity.userMust().getId(), anchorId));
    }

}
