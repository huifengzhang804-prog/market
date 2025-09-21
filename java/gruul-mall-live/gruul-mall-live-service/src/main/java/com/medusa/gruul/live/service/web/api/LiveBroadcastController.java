package com.medusa.gruul.live.service.web.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.live.api.entity.LiveGoodsExamine;
import com.medusa.gruul.live.api.entity.LiveMember;
import com.medusa.gruul.live.api.entity.LiveRoom;
import com.medusa.gruul.live.service.model.dto.*;
import com.medusa.gruul.live.service.service.LiveBroadcastService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author miskw
 * date 2022/11/8
 */
@RestController
@RequestMapping("/live/broadcast")
@PreAuthorize("@S.anyPerm('marketingApp:liveStream','marketingApp:LiveStream')")
@RequiredArgsConstructor
public class LiveBroadcastController {

    private final LiveBroadcastService liveBroadcastService;

    /**
     * 上传素材
     *
     * @param file 文件
     * @return 素材id
     */
    @PostMapping("/{suffix}/uploadSourceMaterial")
    @Log("上传素材")
    @PreAuthorize("@S.shopPerm('marketingApp:LiveStream')")
    public Result<String> uploadSourceMaterial(@RequestParam("file") MultipartFile file,@PathVariable("suffix") String suffix) {
        return Result.ok(liveBroadcastService.uploadSourceMaterial(file,suffix));
    }

    /**
     * 添加并审核商品
     *
     * @param dto 商品信息
     * @return void
     */
    @PostMapping("/goodsAdd")
    @Log("添加并审核商品")
    @Idem(1000)
    public Result<Void> goodsAdd(@RequestBody @Valid AddGoodsDto dto) {
        liveBroadcastService.goodsAdd(dto);
        return Result.ok();
    }

    /**
     * 获取直播商品
     *
     * @param getGoodsDto 查询条件
     * @return 商品列表
     */
    @GetMapping("/getGoods")
    @Log("获取直播商品")
    public Result<IPage<LiveGoodsExamine>> getGoods(GetGoodsDto getGoodsDto) {
        return Result.ok(liveBroadcastService.getGoods(getGoodsDto));
    }

    /**
     * 撤回商品审核
     *
     * @param goodsId 微信GoodsId
     * @param auditId 微信审核单Id
     * @return void
     */
    @PutMapping("/goodsResetAudit/{goodsId}/{auditId}")
    @Log("撤回商品审核")
    public Result<Void> goodsResetAudit(@PathVariable Integer goodsId, @PathVariable Long auditId) {
        liveBroadcastService.goodsResetAudit(goodsId, auditId);
        return Result.ok();
    }

    /**
     * 修改直播商品
     *
     * @param dto 商品信息
     * @return void
     */
    @PutMapping("/goodsUpdate")
    @Log("修改直播商品")
    public Result<Void> goodsUpdate(@RequestBody AddGoodsDto dto) {
        liveBroadcastService.goodsUpdate(dto);
        return Result.ok();
    }

    /**
     * 获取直播商品详情
     *
     * @param goodsId 商品id
     * @return 商品详情
     */
    @GetMapping("/get/goodsInfo/{goodsId}")
    @Log("获取直播商品详情")
    public Result<LiveGoodsExamine> getGoodsInfo(@PathVariable("goodsId") Long goodsId) {
        return Result.ok(liveBroadcastService.getGoodsInfo(goodsId));
    }


    /**
     * 重新提交商品审核
     *
     * @param goodsId 微信GoodsId
     * @return void
     */
    @PutMapping("/resubmitAudit/{goodsId}")
    @Log("重新提交商品审核")
    @Idem(1000)
    public Result<Void> resubmitAudit(@PathVariable Integer goodsId) {
        liveBroadcastService.resubmitAudit(goodsId);
        return Result.ok();
    }


    /**
     * 设置成员角色
     *
     * @param roleDto 角色信息
     * @return void
     */
    @PostMapping("/addRole")
    @Log("设置成员角色")
    @Idem(1000)
    public Result<Void> addRole(@RequestBody RoleDto roleDto) {
        liveBroadcastService.addRole(roleDto);
        return Result.ok();
    }

    /**
     * 直播间成员列表
     *
     * @param liveMemberDto 查询条件
     * @return 成员列表
     */
    @GetMapping("/getLiveMember")
    @Log("直播间成员列表")
    public Result<IPage<LiveMember>> getLiveMember(LiveMemberDto liveMemberDto) {
        return Result.ok(liveBroadcastService.getLiveMember(liveMemberDto));
    }

    /**
     * 批量删除主播
     *
     * @param liveUserIds 主播id
     * @return void
     */
    @DeleteMapping("/delete/liveUser/{liveUserIds}")
    @Log("批量删除主播")
    public Result<Void> deleteLiveUser(@PathVariable("liveUserIds") Long[] liveUserIds) {
        liveBroadcastService.deleteLiveUser(liveUserIds);
        return Result.ok();
    }

    /**
     * 创建直播间
     *
     * @param wxMaLiveRoomDto 直播间信息
     * @return void
     */
    @PostMapping("/createRoom")
    @Log("创建直播间")
    @Idem(1000)
    public Result<Void> createRoom(@RequestBody WxMaLiveRoomDto wxMaLiveRoomDto) {
        liveBroadcastService.createRoom(wxMaLiveRoomDto);
        return Result.ok();
    }

    /**
     * 批量删除直播间
     *
     * @param roomIds 直播间id
     * @return void
     */
    @DeleteMapping("/deleteRoom/{roomIds}")
    @Log("批量删除直播间")
    public Result<Void> deleteRoom(@PathVariable("roomIds") Long[] roomIds) {
        liveBroadcastService.deleteRoom(roomIds);
        return Result.ok();
    }

    /**
     * 直播间列表
     *
     * @param liveInfoDto 查询条件
     * @return 直播间列表
     */
    @GetMapping("/getLiveList")
    @Log("直播间列表")
    public Result<IPage<LiveRoom>> getLiveList(LiveInfoDto liveInfoDto) {
        return Result.ok(liveBroadcastService.getLiveList(liveInfoDto));
    }

    /**
     * 获取直播间详情
     *
     * @param id 直播间id
     * @return 直播间详情
     */
    @GetMapping("/getLiveInfo")
    @Log("获取直播间详情")
    public Result<LiveRoom> getLiveInfo(@RequestParam("id") String id) {
        return Result.ok(liveBroadcastService.getLiveInfo(id));
    }

    /**
     * 获取直播间商品
     *
     * @param dto 查询条件
     * @return 商品列表
     */
    @GetMapping("/getRoom/goods")
    @Log("获取直播间商品")
    public Result<IPage<LiveGoodsExamine>> getRoomGoods(BroadcastRoomGoodsDto dto) {
        IPage<LiveGoodsExamine> page = liveBroadcastService.getRoomGoods(dto);
        return Result.ok(page);
    }

    /**
     * 直播间导入商品
     *
     * @param addLiveGoodsDto 商品信息
     * @return void
     */
    @PostMapping("/addRoom/goods")
    @Log("直播间导入商品")
    @Idem(1000)
    public Result<Void> addRoomGoods(@RequestBody @Valid AddLiveGoodsDto addLiveGoodsDto) {
        liveBroadcastService.addRoomGoods(addLiveGoodsDto);
        return Result.ok();
    }

    /**
     * 批量删除直播商品
     *
     * @param goodsIds 商品id
     * @return void
     */
    @DeleteMapping("/deleteGoodsInfos/{goodsIds}")
    @Log("批量删除直播商品")
    public Result<Void> deleteGoodsInfos(@PathVariable("goodsIds") Long[] goodsIds) {
        liveBroadcastService.deleteGoodsInfos(goodsIds);
        return Result.ok();
    }

}
