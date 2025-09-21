package com.medusa.gruul.addon.live.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.live.constant.LiveConstants;
import com.medusa.gruul.addon.live.enums.AnchorStatus;
import com.medusa.gruul.addon.live.enums.LiveRoomStatus;
import com.medusa.gruul.addon.live.enums.ProhibitedType;
import com.medusa.gruul.addon.live.function.handler.ban.ProhibitedTypeAnnotation;
import com.medusa.gruul.addon.live.model.*;
import com.medusa.gruul.addon.live.mp.entity.*;
import com.medusa.gruul.addon.live.mp.service.*;
import com.medusa.gruul.addon.live.param.AnchorParam;
import com.medusa.gruul.addon.live.param.FollowLiveParam;
import com.medusa.gruul.addon.live.properties.AddressKeyProperties;
import com.medusa.gruul.addon.live.service.LiveUserService;
import com.medusa.gruul.addon.live.socket.service.ISocketService;
import com.medusa.gruul.addon.live.vo.AnchorVO;
import com.medusa.gruul.addon.live.vo.FollowLiveVO;
import com.medusa.gruul.addon.live.vo.LiveRoomVO;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.web.handler.Handler;
import com.medusa.gruul.common.web.util.SpringUtils;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.live.v20180801.LiveClient;
import com.tencentcloudapi.live.v20180801.models.ForbidLiveStreamRequest;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author miskw
 * @date 2023/6/6
 * @describe 用户
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LiveUserServiceImpl implements LiveUserService {
    private final BaseLiveService baseLiveService;
    private final ReservationService reservationService;
    private final AnchorFollowService anchorFollowService;
    private final AnchorService anchorService;
    private final LiveClient liveClient;
    private final AddressKeyProperties addressKeyProperties;
    private final ProhibitedService prohibitedService;
    private final UaaRpcService uaaRpcService;
    private final LiveExtendService liveExtendService;
    private final ISocketService socketService;

    @Override
    public void addReservation(ReservationLiveDTO reservationLiveDto) {
        SecureUser secureUser = ISecurity.userMust();
        Long userId = secureUser.getId();
        String openid = secureUser.getOpenid();
        BaseLive baseLive = Option.of(baseLiveService.lambdaQuery()
                        .eq(BaseLive::getId, reservationLiveDto.getLiveId())
                        .eq(BaseLive::getShopId, reservationLiveDto.getShopId())
                        .one())
                .getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, SystemCode.DATA_NOT_EXIST.getMsg()));
        Long anchorId = baseLive.getAnchorId();
        Anchor anchor = Option.of(anchorService.lambdaQuery().eq(Anchor::getId, anchorId).one())
                .getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, SystemCode.DATA_NOT_EXIST.getMsg()));
        if (anchor.getUserId().equals(userId)) {
            throw new GlobalException(SystemCode.REQ_REJECT_CODE, "请不要自己预约自己的直播间");
        }
        if (baseLive.getStatus() != LiveRoomStatus.NOT_STARTED) {
            throw new GlobalException(SystemCode.REQ_REJECT_CODE, "只能预约未开播的直播间");
        }
        boolean exists = reservationService.lambdaQuery()
                .eq(Reservation::getLiveId, baseLive.getId())
                .eq(Reservation::getUserId, userId)
                .eq(Reservation::getShopId, baseLive.getShopId())
                .exists();
        //已经关注且是关注行为 或者取消关注且没有关注
        if (exists && reservationLiveDto.getIsReservation()) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "已经预约不要再次预约");
        }
        if (!exists && !reservationLiveDto.getIsReservation()) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "已经取消预约不要再次取消预约");
        }
        //预约
        if (reservationLiveDto.getIsReservation()) {
            Reservation reservation = new Reservation()
                    .setUserId(userId)
                    .setOpenid(openid)
                    .setLiveId(baseLive.getId())
                    .setShopId(baseLive.getShopId());
            if (!reservationService.save(reservation)) {
                throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, SystemCode.DATA_ADD_FAILED.getMsg());
            }
            return;
        }
        //取消预约
        boolean remove = reservationService.lambdaUpdate()
                .eq(Reservation::getLiveId, baseLive.getId())
                .eq(Reservation::getUserId, userId)
                .eq(Reservation::getShopId, baseLive.getShopId())
                .remove();
        if (!remove) {
            throw new GlobalException(SystemCode.DATA_DELETE_FAILED_CODE, SystemCode.DATA_DELETE_FAILED.getMsg());
        }
    }

    /**
     * 关注直播间列表
     *
     * @param followLiveParam 参数
     * @return 关注直播间列表
     */
    @Override
    public IPage<FollowLiveVO> followLiveList(FollowLiveParam followLiveParam, Long userId) {
        return anchorFollowService.followLiveList(followLiveParam, userId);
    }

    /**
     * 用户发现直播间
     *
     * @param followLiveParam 参数
     */
    @Override
    public IPage<FollowLiveVO> discoverLiveList(FollowLiveParam followLiveParam, Long userId) {
        return anchorFollowService.discoverLiveList(followLiveParam, userId);
    }

    /**
     * 关注主播
     *
     * @param followAnchorDto 主播信息
     */
    @Override
    public void addFollow(FollowAnchorDTO followAnchorDto) {
        Long userId = ISecurity.userMust().getId();
        Anchor anchor = Option.of(anchorService.getById(followAnchorDto.getAnchorId())
        ).getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, SystemCode.DATA_NOT_EXIST.getMsg()));
        if (userId.equals(anchor.getUserId())) {
            throw new GlobalException(SystemCode.REQ_REJECT_CODE, "请不要自己关注自己");
        }
        boolean exists = anchorFollowService.lambdaQuery()
                .eq(AnchorFollow::getUserId, userId)
                .eq(AnchorFollow::getShopId, anchor.getShopId())
                .eq(AnchorFollow::getAnchorId, followAnchorDto.getAnchorId())
                .exists();
        if ((followAnchorDto.getIsFollow() && exists)) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "已经关注不要再次关注");
        }
        if (!followAnchorDto.getIsFollow() && !exists) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "已经取消关注不要再次取消关注");
        }

        if (followAnchorDto.getIsFollow()) {
            AnchorFollow anchorFollow = new AnchorFollow()
                    .setAnchorId(anchor.getId())
                    .setShopId(anchor.getShopId())
                    .setUserId(userId);
            if (!anchorFollowService.save(anchorFollow)) {
                throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, SystemCode.DATA_ADD_FAILED.getMsg());
            }
            return;
        }
        boolean remove = anchorFollowService.lambdaUpdate()
                .eq(AnchorFollow::getUserId, userId)
                .eq(AnchorFollow::getAnchorId, anchor.getId())
                .eq(AnchorFollow::getShopId, anchor.getShopId())
                .remove();
        if (!remove) {
            throw new GlobalException(SystemCode.DATA_DELETE_FAILED_CODE, SystemCode.DATA_DELETE_FAILED.getMsg());
        }
    }

    /**
     * 管理端主播列表
     *
     * @param anchorParam 主播列表查询参数
     * @return 分页主播
     */
    @Override
    public IPage<AnchorVO> anchorPage(AnchorParam anchorParam) {
        return anchorService.anchorPage(anchorParam);
    }

    /**
     * 启用/禁用主播
     *
     * @param id       主播id
     * @param isEnable 启用 | 禁用
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAnchorStatus(Long id, Boolean isEnable) {
        Anchor anchor = Option.of(anchorService.getById(id))
                .getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST.getCode(), SystemCode.DATA_NOT_EXIST.getMsg()));
        if (anchor.getStatus() == AnchorStatus.VIOLATION) {
            return;
        }
        if (isEnable) {
            anchor.setStatus(AnchorStatus.NORMAL);
        } else {
            anchor.setStatus(AnchorStatus.FORBIDDEN);
        }
        if (!anchorService.updateById(anchor)) {
            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED.getCode(), SystemCode.DATA_UPDATE_FAILED.getMsg());
        }
        if (isEnable) {
            return;
        }
        List<BaseLive> baseLives = getBaseLives(anchor);
        if (CollUtil.isEmpty(baseLives)) {
            return;
        }
        for (BaseLive baseLive : baseLives) {
            baseLive.setEndTime(LocalDateTime.now())
                    .setStatus(LiveRoomStatus.OVER);
            //直播间禁播
            tencentPushAddress(baseLive.getStreamName(), "店铺管理员禁用主播");
            liveExtendService.handlerExtend(baseLive);
        }
        socketService.closeSocketChannel(baseLives.stream().map(BaseLive::getId).map(String::valueOf).collect(Collectors.toList()));
        if (!baseLiveService.updateBatchById(baseLives)) {
            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED.getCode(), SystemCode.DATA_UPDATE_FAILED.getMsg());
        }
        //删除直播店铺id
        RedisUtil.delete(RedisUtil.key(LiveConstants.LIVE_SHOP, anchor.getUserId()));
    }

    /**
     * 平台 恢复/违规禁播主播
     *
     * @param updateAnchorDto 恢复/违规主播参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAnchorStatusForPlatform(UpdateAnchorDTO updateAnchorDto) {
        updateAnchorDto.validParam();
        Handler<List<BaseLive>> handler = SpringUtils.getBean(ProhibitedTypeAnnotation.class, updateAnchorDto.getType());
        List<BaseLive> baseLives = handler.handle(updateAnchorDto);
        if (CollUtil.isEmpty(baseLives)) {
            return;
        }
        Long userId = ISecurity.userMust().getId();
        UserInfoVO userInfoVO = uaaRpcService.getUserDataByUserId(userId)
                .getOrElseThrow(() -> new GlobalException(SystemCode.REQ_REJECT_CODE, "质检员用户为空"));
        List<Prohibited> prohibitedList = new ArrayList<>();
        for (BaseLive baseLive : baseLives) {
            baseLive.setEndTime(LocalDateTime.now())
                    .setStatus(LiveRoomStatus.ILLEGAL_SELL_OFF);
            //直播间禁播
            tencentPushAddress(baseLive.getStreamName(), updateAnchorDto.getReason());
            liveExtendService.handlerExtend(baseLive);
            Prohibited prohibited = new Prohibited()
                    .setQualityInspector(userInfoVO.getNickname())
                    .setType(ProhibitedType.LIVE)
                    .setShopId(baseLive.getShopId())
                    .setCategoryTypes(updateAnchorDto.getCategoryTypes())
                    .setSourceId(baseLive.getId())
                    .setReason(updateAnchorDto.getReason())
                    .setRelevantEvidence(updateAnchorDto.getRelevantEvidence());
            prohibitedList.add(prohibited);
            if (!baseLiveService.updateById(baseLive)) {
                throw new GlobalException(SystemCode.DATA_UPDATE_FAILED.getCode(), SystemCode.DATA_UPDATE_FAILED.getMsg());
            }
        }
        socketService.closeSocketChannel(baseLives.stream().map(BaseLive::getId).map(String::valueOf).collect(Collectors.toList()));
        if (!prohibitedService.saveBatch(prohibitedList)) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED.getCode(), SystemCode.DATA_ADD_FAILED.getMsg());
        }
    }

    /**
     * 查看违禁原因
     *
     * @param sourceId 来源id
     * @param type     来源类型 直播 | 主播
     * @return 违禁原因
     */
    @Override
    public Prohibited banReason(Long sourceId, ProhibitedType type) {
        return prohibitedService.banReason(sourceId, type);
    }

    /**
     * 获取主播信息
     *
     * @param userId 用户id
     * @return 主播信息
     */
    @Override
    public AnchorVO anchorMessage(Long userId) {
        return anchorService.anchorMessage(userId);
    }

    /**
     * 新增主播
     *
     * @param addAnchorDto 主播新增参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAnchor(AddAnchorDTO addAnchorDto) {
        Long shopId = ISecurity.userMust().getShopId();
        Anchor anchor = anchorService.lambdaQuery()
                .eq(Anchor::getUserId, addAnchorDto.getUserId())
                .one();
        if (BeanUtil.isNotEmpty(anchor)) {
            throw new GlobalException(SystemCode.DATA_EXISTED_CODE, SystemCode.DATA_EXISTED.getMsg());
        }
        Option<UserInfoVO> userDataByUserId = uaaRpcService.getUserDataByUserId(addAnchorDto.getUserId());
        UserInfoVO userInfoVO = userDataByUserId.getOrElseThrow(() -> new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, SystemCode.DATA_UPDATE_FAILED.getMsg()));
        Anchor newAnchor = new Anchor()
                .setAnchorIcon(userInfoVO.getAvatar())
                .setShopId(shopId)
                .setUserId(addAnchorDto.getUserId())
                .setGender(userInfoVO.getGender())
                .setAnchorNickname(userInfoVO.getNickname())
                .setAnchorSynopsis(addAnchorDto.getAnchorSynopsis())
                .setPhone(userInfoVO.getMobile())
                .setStatus(AnchorStatus.NORMAL);
        if (!anchorService.save(newAnchor)) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, SystemCode.DATA_UPDATE_FAILED.getMsg());
        }
        //添加主播角色
        uaaRpcService.addAnchorAuthority(userInfoVO.getUserId());
    }

    /**
     * 用户随机获取一条直播间信息
     *
     * @param id 直播间id
     * @return 直播间信息
     */
    @Override
    public LiveRoomVO randomView(Long id) {
        return baseLiveService.randomView(id);
    }

    /**
     * 直播间互动数据落库
     *
     * @param liveIds 直播间ids
     */
    @Override
    public void saveInteractionDbBatch(Set<String> liveIds) {
        List<LiveExtend> liveExtendList = liveIds.stream()
                .map(Long::valueOf)
                .map(liveId -> {
                    Integer likeCount = (Integer) Option.of(RedisUtil.getCacheObject(RedisUtil.key(LiveConstants.LIVE_LIKE, liveId.toString()))).getOrElse(0);
                    Integer viewershipCount = (Integer) Option.of(RedisUtil.getCacheObject(RedisUtil.key(LiveConstants.LIVE_VIEWERSHIP, liveId.toString()))).getOrElse(0);
                    if (likeCount == 0 && viewershipCount == 0) {
                        return null;
                    }
                    LiveExtend liveExtend = liveExtendService.lambdaQuery()
                            .eq(LiveExtend::getLiveId, liveId)
                            .one();
                    if (liveExtend == null) {
                        BaseLive baseLive = Option.of(baseLiveService.getById(liveId))
                                .getOrElseThrow(
                                        () -> new GlobalException(SystemCode.DATA_NOT_EXIST.getCode(), SystemCode.DATA_NOT_EXIST.getMsg())
                                );
                        return new LiveExtend()
                                .setLikeCount(likeCount)
                                .setViewership(viewershipCount)
                                .setShopId(baseLive.getShopId())
                                .setLiveId(liveId);
                    }
                    if (!ObjectUtil.equals(liveExtend.getViewership(), viewershipCount) || !liveExtend.getLikeCount().equals(likeCount)) {
                        liveExtend.setLikeCount(likeCount)
                                .setViewership(viewershipCount);
                        return liveExtend;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (CollUtil.isNotEmpty(liveExtendList)) {
            liveExtendService.saveOrUpdateBatch(liveExtendList);
        }
    }

    /**
     * 添加观看人数数据到redis
     *
     * @param liveId 直播间id
     */
    @Override
    @Redisson(name = LiveConstants.LIVE_VIEWERSHIP_LOCK, key = "#liveId")
    public void addViewership(String liveId) {
        if (StrUtil.isEmpty(liveId)) {
            return;
        }
        String redisKey = RedisUtil.key(LiveConstants.LIVE_VIEWERSHIP, liveId);
        Integer cacheObject = (Integer) Option.of(
                RedisUtil.getCacheObject(redisKey)
        ).getOrElse(0);
        RedisUtil.setCacheObject(redisKey, cacheObject + 1, 24,
                TimeUnit.HOURS
        );
    }

    /**
     * 用户是否关注 前端需要过滤一遍用户登陆状态
     *
     * @param userId 用户id
     * @return 是否关注
     */
    @Override
    public Boolean viewershipStatus(Long userId, Long anchorId) {
        return anchorFollowService.lambdaQuery()
                .eq(AnchorFollow::getUserId, userId)
                .eq(AnchorFollow::getAnchorId, anchorId)
                .exists();
    }

    /**
     * 获取未开播与进行中的直播间
     *
     * @param anchor 主播
     * @return 直播间列表
     */
    private List<BaseLive> getBaseLives(Anchor anchor) {
        return baseLiveService.lambdaQuery()
                .eq(BaseLive::getAnchorId, anchor.getId())
                .and(
                        qw -> qw.eq(BaseLive::getStatus, LiveRoomStatus.NOT_STARTED)
                                .or(orQw -> orQw.eq(BaseLive::getStatus, LiveRoomStatus.PROCESSING)))
                .list();
    }

    /**
     * 腾讯云推流禁用
     *
     * @param streamName streamName
     * @param reason     禁播原因
     */
    public void tencentPushAddress(String streamName, String reason) {
        ForbidLiveStreamRequest request = new ForbidLiveStreamRequest();
        request.setAppName("live");
        request.setReason(reason);
        request.setDomainName(addressKeyProperties.getPushAuthentication());
        request.setStreamName(streamName);
        try {
            liveClient.ForbidLiveStream(request);
        } catch (TencentCloudSDKException e) {
            log.error("-----------------禁用失败---------");
            log.error(e.getMessage());
        }
    }
}
