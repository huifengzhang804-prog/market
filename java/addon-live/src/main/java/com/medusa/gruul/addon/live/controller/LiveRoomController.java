package com.medusa.gruul.addon.live.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.live.model.PreviewLiveRoomDTO;
import com.medusa.gruul.addon.live.service.AddonLiveRoomService;
import com.medusa.gruul.addon.live.vo.LiveAnchorVO;
import com.medusa.gruul.addon.live.vo.LiveRoomMessageVO;
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
 * 主播直播间控制器
 *
 * @author miskw
 * @date 2023/6/3
 * @describe 主播直播间控制器
 */
@RestController
@RequestMapping("/live/room")
@RequiredArgsConstructor
public class LiveRoomController {
    private final AddonLiveRoomService liveRoomService;

    /**
     * 查询是否有已经开播的直播间
     *
     * @param id 直播间id
     * @return 是否有已经开播的直播间
     */
    @Idem(500)
    @Log("是否有已经开播的直播间")
    @GetMapping("/begin/{id}")
    @PreAuthorize(value = "@S.matcher().eq(@S.SUB_ROLES,@S.ANCHOR).match()")
    public Result<Boolean> beginLive(@PathVariable Long id) {
        Long userId = ISecurity.userMust().getId();
        return Result.ok(liveRoomService.beginLive(userId, id));
    }

    /**
     * 创建直播间
     *
     * @param previewLiveRoomDTO 创建直播参数
     * @return 直播间id、推流地址
     */
    @Log("创建直播间")
    @PostMapping("/create")
    @PreAuthorize(value = "@S.matcher().eq(@S.SUB_ROLES,@S.ANCHOR).match()")
    public Result<LiveRoomMessageVO> createLiveRoom(@Valid @RequestBody PreviewLiveRoomDTO previewLiveRoomDTO) {
        return Result.ok(liveRoomService.createLiveRoom(previewLiveRoomDTO));

    }

    /**
     * 查询主播对应的直播间列表
     *
     * @param page 分页
     */
    @Log("查询主播对应的直播间列表")
    @GetMapping("/anchor/list")
    @PreAuthorize(value = "@S.matcher().eq(@S.SUB_ROLES,@S.ANCHOR).match()")
    public Result<IPage<LiveAnchorVO>> liveAnchorList(Page<LiveAnchorVO> page) {
        Long userId = ISecurity.userMust().getId();
        return Result.ok(liveRoomService.liveAnchorList(page, userId));
    }

    /**
     * 直播间下播
     *
     * @param id 直播间id
     */
    @Log("直播间下播")
    @PutMapping("/lower/broadcast/{id}")
    @PreAuthorize(value = "@S.matcher().eq(@S.SUB_ROLES,@S.ANCHOR).match()")
    public Result<Void> lowerBroadcast(@PathVariable Long id) {
        liveRoomService.lowerBroadcast(id, ISecurity.userMust().getId());
        return Result.ok();
    }

    /**
     * 删除直播间
     *
     * @param id 直播间id
     */
    @Log("删除直播间")
    @DeleteMapping("/deleted/{id}")
    @PreAuthorize(value = "@S.matcher().eq(@S.SUB_ROLES,@S.ANCHOR).match()")
    public Result<Void> deletedLiveById(@PathVariable Long id) {
        liveRoomService.deletedLiveById(id, ISecurity.userMust().getId());
        return Result.ok();
    }

    /**
     * 直播间详情
     *
     * @param id 直播间id
     * @return 直播间详情
     */
    @Log("直播间详情")
    @GetMapping("/detail/{id}")
    public Result<LiveRoomVO> detail(@PathVariable Long id) {
        return Result.ok(liveRoomService.detail(id));
    }

    /**
     * 直播间聊天室userSig
     *
     * @param userId 用户 ID
     * @return 生成的签名
     */
    @Log("生成直播间聊天室userSig")
    @GetMapping("/userSig/{userId}")
    public Result<String> chatRoomUserSig(@PathVariable String userId) {
        return Result.ok(liveRoomService.chatRoomUserSig(userId));
    }
}
