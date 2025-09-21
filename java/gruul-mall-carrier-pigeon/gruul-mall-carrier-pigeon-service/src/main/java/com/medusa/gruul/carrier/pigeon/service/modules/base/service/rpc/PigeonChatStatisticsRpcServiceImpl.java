package com.medusa.gruul.carrier.pigeon.service.modules.base.service.rpc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.carrier.pigeon.api.model.dto.UploadParamDTO;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.carrier.pigeon.service.im.constants.IMRedisConstant;
import com.medusa.gruul.carrier.pigeon.service.im.entity.ChatMessage;
import com.medusa.gruul.carrier.pigeon.service.im.entity.GroupChatRoom;
import com.medusa.gruul.carrier.pigeon.service.im.repository.GroupChatRoomRepository;
import com.medusa.gruul.carrier.pigeon.service.im.repository.MessageRepository;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.FileHelper;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.enums.StorageType;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.mp.service.IOssConfService;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.service.SmsSignService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>信鸽聊天统计RPC接口</p>
 * <p>提供如下统计接口</p>
 * <p>获取当天咨询供应商的店铺数量:{@link PigeonChatStatisticsRpcService#getInquiryNumber(Long)}</p>
 *
 * @author An.Yan
 */
@Slf4j
@Service
@DubboService
@RequiredArgsConstructor
public class PigeonChatStatisticsRpcServiceImpl implements PigeonChatStatisticsRpcService {

    private final GroupChatRoomRepository groupChatRoomRepository;
    private final ShopRpcService shopRpcService;
    private final MessageRepository messageRepository;
    private final FileStorageService fileStorageService;
    private final IOssConfService systemConfService;
    private final SmsSignService smsSignService;

    /**
     * 获取当天咨询供应商的店铺数量
     *
     * @param shopId 供应商店铺ID
     * @return {@link Integer}
     */
    @Override
    public Integer getInquiryNumber(Long shopId) {
        AtomicReference<Integer> result = new AtomicReference<>(CommonPool.NUMBER_ZERO);
        // 检查店铺信息
        ShopInfoVO shopInfo = shopRpcService.getShopInfoByShopId(shopId);
        if (shopInfo == null || !ShopMode.SUPPLIER.equals(shopInfo.getShopMode())) {
            return result.get();
        }
        LocalDateTime now = LocalDateTime.now();

        Set<String> groupChatRoomSet = groupChatRoomRepository.listGroupChatRoom(new GroupChatRoom(shopId), 0, -1);
        Pattern roomIdPattern = Pattern.compile("^\\d+:\\d+$");
        // 获取每个聊天室的聊天记录
        groupChatRoomSet.forEach(roomObj -> {
            //String roomId = JSON.parseObject(String.valueOf(roomObj), GroupChatRoom.class).getGroupChatRoomValue();
            if (!roomIdPattern.matcher(roomObj).matches()) {
                return;
            }
            Long fromShopId = Long.parseLong(roomObj.split(":")[0]);
            Long toShopId = Long.parseLong(roomObj.split(":")[1]);
            GroupChatRoom groupChatRoomItem = new GroupChatRoom(fromShopId, toShopId, null, null);

            Set<String> messages = this.messageRepository.listChatMessageByRoomId(groupChatRoomItem, 0, 1);
            if (CollectionUtils.isEmpty(messages)) {
                return;
            }
            ChatMessage chatMessage = JSON.parseObject(messages.stream().findFirst().get(), ChatMessage.class);
            if (chatMessage.checkWhetherCreatedToday(now)) {
                result.getAndSet(result.get() + CommonPool.NUMBER_ONE);
            }

        });
        return result.get();
    }

    @Override
    public Map<Integer, String> batchUpload(Map<Integer, UploadParamDTO> uploadParamMap) {
        if (CollUtil.isEmpty(uploadParamMap)) {
            throw new GlobalException("uploadParamMap is empty");
        }
        StorageType storageType = systemConfService.usingConfig();
        if (Objects.isNull(storageType)) {
            //检测是否配置上传组件参数
            throw new GlobalException("平台端OSS参数未配置");
        }
        //获取要上传的平台
        return uploadParamMap.entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> {
                                    UploadParamDTO param = entry.getValue();
                                    FileInfo fileInfo = FileHelper.uploadWithPath(
                                            fileStorageService.of(param.getFileBytes(), "temp." + param.getFormat())
                                    );
                                    return fileInfo.getUrl();
                                }
                        )
                );
    }

    @Override
    public Map<String, String> batchUploadUrls(Set<String> fileUrlStr) {
        if (CollUtil.isEmpty(fileUrlStr)) {
            return Map.of();
        }
        StorageType storageType = systemConfService.usingConfig();
        if (Objects.isNull(storageType)) {
            //检测是否配置上传组件参数
            throw new GlobalException("平台端OSS参数未配置");
        }
        Map<String, String> result = MapUtil.newHashMap(fileUrlStr.size());
        fileUrlStr.stream()
                .filter(Objects::nonNull)
                .forEach(
                        urlStr -> {
                            URL url;
                            try {
                                url = URLUtil.url(urlStr);
                            } catch (Exception ex) {
                                log.error("urlStr is not a valid url:" + urlStr, ex);
                                return;
                            }
                            try {
                              FileInfo upload = FileHelper.uploadWithPath(fileStorageService.of(url));
                              result.put(urlStr, upload.getUrl());
                            }catch (Exception e){
                              log.error("e= {},url= {}",e,url);
                            }

                        }
                );
        return result;
    }

    /**
     * 删除IM中店铺的缓存信息
     * @param shopId
     */
    @Override
    public void removeIMShopInfoCache(Long shopId) {

        RedisUtil.getRedisTemplate().opsForHash().delete(
                IMRedisConstant.IM_SHOP_INFO_KEY,
                String.valueOf(shopId));
    }

    @Override
    public Boolean querySmsSimulationFlag() {
        return smsSignService.querySmsSimulationFlag();
    }

    /**
     * 删除IM中用户的缓存信息
     * @param userId
     */
    @Override
    public void removeIMUserInfoCache(Long userId) {

        RedisUtil.getRedisTemplate().opsForHash().delete(
                IMRedisConstant.IM_USERINFO_KEY,String.valueOf(userId));

    }
}
