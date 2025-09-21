package com.medusa.gruul.carrier.pigeon.api.rpc;

import com.medusa.gruul.carrier.pigeon.api.model.dto.UploadParamDTO;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * <p>信鸽聊天统计RPC接口</p>
 * <p>提供如下统计接口</p>
 * <p>获取当天咨询供应商的店铺数量:{@link PigeonChatStatisticsRpcService#getInquiryNumber(Long)}</p>
 *
 * @author An.Yan
 */
public interface PigeonChatStatisticsRpcService {

    /**
     * 获取当天咨询供应商的店铺数量
     *
     * @param shopId 供应商店铺ID
     * @return {@link Integer}
     */
    Integer getInquiryNumber(Long shopId);

    /**
     * 批量上传文件 返回文件路径
     * todo 不是最优解决方案，后续考虑优化
     *
     * @param uploadParamMap key:文件下标 value:文件参数
     * @return 文件路径 key:文件下标 value:文件路径
     */
    Map<Integer, String> batchUpload(@NotNull Map<Integer, UploadParamDTO> uploadParamMap);


    /**
     * 批量根据文件 url上传文件
     *
     * @param fileUrls 文件 url
     * @return 系统内的文件 url key:原文件 url value:oss系统内文件 url
     */
    Map<String, String> batchUploadUrls(Set<String> fileUrls);

    /**
     * 删除IM中用户的缓存信息
     * @param userId
     */
    void removeIMUserInfoCache(Long userId);

    /**
     * 删除IM中店铺的缓存信息
     * @param shopId
     */
    void removeIMShopInfoCache(Long shopId);

    /**
     * 查询短信发送模拟、真实的标记
     * @return
     */
    Boolean querySmsSimulationFlag();
}
