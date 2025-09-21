package com.medusa.gruul.live.service.web.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.live.api.dto.AppletsLiveRoomDto;
import com.medusa.gruul.live.api.dto.LiveRoomDto;
import com.medusa.gruul.live.api.dto.LiveUserDto;
import com.medusa.gruul.live.api.dto.PlatformLiveGoodsDto;
import com.medusa.gruul.live.api.vo.AppletsLiveRoomVo;
import com.medusa.gruul.live.api.vo.LiveGoodsVo;
import com.medusa.gruul.live.api.vo.LiveRoomVo;
import com.medusa.gruul.live.api.vo.LiveUserVo;
import com.medusa.gruul.live.service.service.LiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author miskw
 * date 2022/11/16
 * describe 平台端直播控制器
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("@S.platformPerm('marketingApp:liveStream')")
@RequestMapping("platform/live")
public class LiveController {
	private final LiveService liveService;

	/**
	 * 平台查询直播间列表
	 *
	 * @param liveRoomDto 直播间查询条件
	 * @return 直播间列表
	 */
	@GetMapping("/liveRoom")
	@Log("平台查询直播间列表")
	public Result<IPage<LiveRoomVo>> liveRoom(LiveRoomDto liveRoomDto) {
		return Result.ok(liveService.liveRoom(liveRoomDto));
	}

	/**
	 * 平台批量删除直播间
	 *
	 * @param roomIds 直播间id
	 * @return void
	 */
	@DeleteMapping("/liveRoom/{roomIds}")
	@Log("平台批量删除直播间")
	public Result<Void> deleteLiveRoomToPlatform(@PathVariable("roomIds") Long[] roomIds) {
		liveService.deleteLiveRoomToPlatform(roomIds);
		return Result.ok();
	}

	/**
	 * 分享直播间
	 *
	 * @param roomId 直播间id
	 * @return 分享链接
	 */
	@GetMapping("/share/live/room")
	@Log("分享直播间")
	public Result<String> shareLiveRoom(Long roomId) {
		return Result.ok(liveService.shareLiveRoom(roomId));
	}

	/**
	 * 平台查询所有商品列表
	 *
	 * @param platformLiveGoodsDto 商品查询条件
	 * @return 商品列表
	 */
	@GetMapping("/liveGoods")
	@Log("平台查询所有商品列表")
	public Result<IPage<LiveGoodsVo>> liveGoods(PlatformLiveGoodsDto platformLiveGoodsDto) {
		return Result.ok(liveService.liveGoods(platformLiveGoodsDto));
	}

	/**
	 * 平台批量删除商品
	 *
	 * @param goodsIds 商品id
	 * @return void
	 */
	@DeleteMapping("/delete/goods/{goodsIds}")
	@Log("平台批量删除商品")
	public Result<Void> deleteLiveGoods(@PathVariable("goodsIds") Long[] goodsIds) {
		liveService.deleteLiveGoods(goodsIds);
		return Result.ok();
	}

	/**
	 * 平台查询所有主播成员
	 *
	 * @param dto 查询条件
	 * @return 主播成员列表
	 */
	@GetMapping("/get/liveUser")
	@Log("平台查询所有主播成员")
	public Result<IPage<LiveUserVo>> getLiveUser(LiveUserDto dto) {
		return Result.ok(liveService.getLiveUser(dto));
	}

	/**
	 * 批量删除直播成员
	 *
	 * @param roomUserIds 直播成员id
	 * @return void
	 */
	@DeleteMapping("/delete/liveUser/{roomUserIds}")
	@Log("批量删除直播成员")
	public Result<Void> deleteLiveUser(@PathVariable("roomUserIds") Long[] roomUserIds) {
		liveService.deleteLiveUser(roomUserIds);
		return Result.ok();
	}

	/**
	 * 小程序直播间
	 *
	 * @return 直播间列表
	 */
	@GetMapping("/applets/liveRoom")
	@Log("小程序直播间")
	@PreAuthorize("permitAll()")
	public Result<IPage<AppletsLiveRoomVo>> getAppletsLiveRoom(AppletsLiveRoomDto dto) {
		return Result.ok(liveService.getAppletsLiveRoom(dto));
	}


}
