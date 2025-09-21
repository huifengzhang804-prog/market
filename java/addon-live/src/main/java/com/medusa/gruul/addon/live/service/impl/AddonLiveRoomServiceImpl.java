package com.medusa.gruul.addon.live.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.live.constant.LiveConstants;
import com.medusa.gruul.addon.live.enums.AnchorStatus;
import com.medusa.gruul.addon.live.enums.LiveNotify;
import com.medusa.gruul.addon.live.enums.LiveRoomStatus;
import com.medusa.gruul.addon.live.function.LiveNotifyAnnotation;
import com.medusa.gruul.addon.live.model.PreviewLiveRoomDTO;
import com.medusa.gruul.addon.live.model.PreviewLiveRoomInstantDTO;
import com.medusa.gruul.addon.live.mp.entity.Anchor;
import com.medusa.gruul.addon.live.mp.entity.BaseLive;
import com.medusa.gruul.addon.live.mp.service.AnchorService;
import com.medusa.gruul.addon.live.mp.service.BaseLiveService;
import com.medusa.gruul.addon.live.mp.service.LiveExtendService;
import com.medusa.gruul.addon.live.param.LiveNotifyParam;
import com.medusa.gruul.addon.live.param.LiveRoomParam;
import com.medusa.gruul.addon.live.properties.AddressKeyProperties;
import com.medusa.gruul.addon.live.service.AddonLiveRoomService;
import com.medusa.gruul.addon.live.socket.service.ISocketService;
import com.medusa.gruul.addon.live.utils.AddressSplicingUtils;
import com.medusa.gruul.addon.live.utils.TLSSigAPIv2;
import com.medusa.gruul.addon.live.vo.LiveAnchorVO;
import com.medusa.gruul.addon.live.vo.LiveRoomMessageVO;
import com.medusa.gruul.addon.live.vo.LiveRoomVO;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.web.handler.Handler;
import com.medusa.gruul.common.web.util.SpringUtils;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import io.vavr.control.Option;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 描述
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddonLiveRoomServiceImpl implements AddonLiveRoomService {
    private final AddressKeyProperties addressKeyProperties;
    private final AnchorService anchorService;
    private final ShopRpcService shopRpcService;
    private final BaseLiveService baseLiveService;
    private final LiveExtendService liveExtendService;
    private final ISocketService socketService;

    /**
     * 查询是否有已经开播的直播间
     *
     * @param userId 用户id
     * @param liveId 直播间id
     * @return 是否有已经开播的直播间
     */
    @Override
    public Boolean beginLive(Long userId, Long liveId) {
        // 当前直播间是否禁播
        BaseLive baseLive = Option.of(baseLiveService.getLiveRoom(liveId, userId))
                .getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST.getCode(), SystemCode.DATA_NOT_EXIST.getMsg()));
        if (baseLive.getStatus() != LiveRoomStatus.NOT_STARTED) {
            throw new GlobalException(SystemCode.REQ_REJECT_CODE, "直播间已被封禁，详情请联系管理员");
        }
        return baseLiveService.isBeginLive(userId);
    }

    /**
     * 创建直播间
     *
     * @param previewLiveRoom 创建直播间参数
     */
    @Override
    public LiveRoomMessageVO createLiveRoom(PreviewLiveRoomDTO previewLiveRoom) {
        boolean immediately = previewLiveRoom.getBeginTime() == null;
        Long userId = ISecurity.userMust().getId();
        Long shopId = shopId(userId);
        if (immediately && ISystem.shopId(shopId, () -> baseLiveService.isBeginLive(userId))) {
            throw new GlobalException(SystemCode.REQ_REJECT_CODE, "已经有开播的直播间");
        }
        previewLiveRoom.validParam();
        previewLiveRoom.setUserId(userId);
        LocalDateTime beginTime = Option.of(previewLiveRoom.getBeginTime())
                .getOrElse(LocalDateTime.now());
        //处理直播间数据
        BaseLive baseLive = handlerLive(previewLiveRoom, beginTime);
        baseLive.setBeginTime(beginTime)
                .setStatus(immediately ? LiveRoomStatus.PROCESSING : LiveRoomStatus.NOT_STARTED);
        boolean success = ISystem.shopId(shopId, () -> baseLiveService.save(baseLive));
        SystemCode.DATA_ADD_FAILED.falseThrow(success);
        return new LiveRoomMessageVO()
                .setId(baseLive.getId())
                .setPushAddress(baseLive.getPushAddress());
    }

    /**
     * 直播回调
     */
    @Override
    public void liveNotify(HttpServletRequest request) throws IOException {
        String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        log.info("回调参数:{}", xmlResult);
        // record || snapshot
        String action = request.getParameter("action");
        LiveNotify liveNotify = Option.of(LiveNotify.getLiveNotify(action))
                .getOrElseThrow(() -> new GlobalException(SystemCode.REQ_REJECT_CODE, "查找不到对应的回调方法"));
        JSONObject entries = JSONUtil.parseObj(xmlResult);
        String streamName = entries.getStr("stream_id");
        String streamParam = entries.getStr("stream_param");
        String domain = entries.getStr("app");
        String appname = entries.getStr("appname");
        Integer eventType = entries.getInt("event_type");
        String rtmp = "rtmp://" + domain + "/" + appname + "/" + streamName + "?" + streamParam;
        LiveNotifyParam liveNotifyParam = new LiveNotifyParam()
                .setEventType(eventType)
                .setPushAddress(rtmp);
        Handler<Boolean> handler = SpringUtils.getBean(LiveNotifyAnnotation.class, liveNotify);
        handler.handle(liveNotifyParam);
    }

    @Override
    public IPage<LiveAnchorVO> liveAnchorList(Page<LiveAnchorVO> page, Long userId) {
        return baseLiveService.liveAnchorList(page, userId);
    }

    /**
     * 删除直播间
     *
     * @param liveId 直播间id
     * @param userId 用户id
     */
    @Override
    public void deletedLiveById(Long liveId, Long userId) {
        BaseLive baseLive = Option.of(TenantShop.disable(() -> baseLiveService.getLiveRoom(liveId, userId)))
                .getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST.getCode(), SystemCode.DATA_NOT_EXIST.getMsg()));
        if (baseLive.getStatus() == LiveRoomStatus.PROCESSING) {
            throw new GlobalException(SystemCode.REQ_REJECT_CODE, "直播中的直播间不能删除");
        }
        if (!baseLiveService.removeById(liveId)) {
            throw new GlobalException(SystemCode.DATA_DELETE_FAILED.getCode(), SystemCode.DATA_DELETE_FAILED.getMsg());
        }
    }

    /**
     * 管理端直播间列表
     *
     * @param liveRoomParam 直播间查询参数
     */
    @Override
    public IPage<LiveRoomVO> livePage(LiveRoomParam liveRoomParam) {
        return baseLiveService.livePage(liveRoomParam);
    }

    /**
     * 直播间详情
     *
     * @param id 直播间id
     * @return 直播间详情
     */
    @Override
    public LiveRoomVO detail(Long id) {
        return baseLiveService.detail(id);
    }

    @Override
    public Long shopId(Long userId) {
        String anchorKey = RedisUtil.key(LiveConstants.LIVE_SHOP, userId);
        return RedisUtil.getCache(
                () -> RedisUtil.getCacheObject(anchorKey),
                () -> {
                    Anchor anchor = TenantShop.disable(
                            () -> anchorService.lambdaQuery()
                                    .select(Anchor::getShopId)
                                    .eq(Anchor::getStatus, AnchorStatus.NORMAL)
                                    .eq(Anchor::getUserId, userId)
                                    .one()
                    );
                    if (anchor == null) {
                        throw new GlobalException(SystemCode.REQ_REJECT_CODE, "当前用户非店铺主播");
                    }
                    return anchor.getShopId();
                },
                Duration.ofDays(1),
                anchorKey
        );
    }

    /**
     * 直播间下播
     *
     * @param liveId 直播间id
     * @param userId 用户id
     */
    @Override
    public void lowerBroadcast(Long liveId, Long userId) {
        BaseLive baseLive = Option.of(TenantShop.disable(() -> baseLiveService.getLiveRoom(liveId, userId)))
                .getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, SystemCode.DATA_NOT_EXIST.getMsg()));
        if (baseLive.getStatus() != LiveRoomStatus.PROCESSING) {
            throw new GlobalException(SystemCode.REQ_REJECT_CODE, "只能关闭直播中的直播间");
        }
        baseLive.setEndTime(LocalDateTime.now())
                .setStatus(LiveRoomStatus.OVER);
        baseLiveService.updateById(baseLive);
        liveExtendService.handlerExtend(baseLive);
        socketService.closeSocketChannel(Collections.singletonList(liveId.toString()));
    }

    /**
     * 直播间聊天室userSig
     *
     * @param userId 用户 ID
     * @return 生成的签名
     */
    @Override
    public String chatRoomUserSig(String userId) {
        return new TLSSigAPIv2(addressKeyProperties.getImAppId(), addressKeyProperties.getImKey())
                .genUserSig(userId, 86400);
    }

    /**
     * 创建直播间公用方法
     *
     * @param previewLiveRoomInstantDto 直播间参数
     * @param dateTime                  开播时间
     * @return 直播间
     */
    private BaseLive handlerLive(PreviewLiveRoomInstantDTO previewLiveRoomInstantDto, LocalDateTime dateTime) {
        Anchor anchor = TenantShop.disable(
                () -> Option.of(anchorService.lambdaQuery()
                                .eq(Anchor::getUserId, previewLiveRoomInstantDto.getUserId())
                                .one())
                        .getOrElseThrow(() -> SystemCode.DATA_EXISTED.msgEx("当前主播不存在")));
        if (anchor.getStatus() != AnchorStatus.NORMAL) {
            throw new GlobalException(SystemCode.REQ_REJECT_CODE, "主播已经被禁播，不能创建直播间");
        }

        //获取唯一steamName
        String streamName = AddressSplicingUtils.getStreamName(dateTime);
        //推流有效时间 时间到了，流没断也可继续直播
        LocalDateTime expiredTime = LocalDateTimeUtil.offset(dateTime, CommonPool.NUMBER_ONE, ChronoUnit.DAYS);
        long expiredSecond = expiredTime.atZone(ZoneId.systemDefault()).toEpochSecond();
        //获取推流地址
        String pushAddress = AddressSplicingUtils.getSafeUrl(addressKeyProperties.getPushAuthenticationKey(), streamName, expiredSecond, addressKeyProperties.getPushAuthentication(), true);
        //拉流结束时间
        String pullAddress = AddressSplicingUtils.getSafeUrl(addressKeyProperties.getPullAuthenticationKey(), streamName, expiredSecond, addressKeyProperties.getPullAuthentication(), false);
        ShopInfoVO infoVO = shopRpcService.getShopInfoByShopId(anchor.getShopId());
        return handlerBaseLive(infoVO, anchor, previewLiveRoomInstantDto, pushAddress, pullAddress, streamName);
    }


    /**
     * 封装数据
     *
     * @return 直播间信息
     */
    private BaseLive handlerBaseLive(ShopInfoVO infoVO,
                                     Anchor anchor,
                                     PreviewLiveRoomInstantDTO previewLiveRoomInstantDto,
                                     String pushAddress,
                                     String pullAddress,
                                     String streamName) {
        return new BaseLive()
                .setAnchorId(anchor.getId())
                .setAnchorNickname(anchor.getAnchorNickname())
                .setShopId(anchor.getShopId())
                .setShopLogo(infoVO.getLogo())
                .setShopName(infoVO.getName())
                .setShopType(infoVO.getShopType())
                .setLiveSynopsis(previewLiveRoomInstantDto.getLiveSynopsis())
                .setLiveTitle(previewLiveRoomInstantDto.getTitle())
                .setPic(previewLiveRoomInstantDto.getPic())
                .setStreamName(streamName)
                .setPushAddress(pushAddress)
                .setPullAddress(pullAddress);

    }
}
