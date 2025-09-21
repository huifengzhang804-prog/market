package com.medusa.gruul.storage.api.rpc;

import com.medusa.gruul.storage.api.dto.activity.ActivityCloseKey;
import com.medusa.gruul.storage.api.dto.activity.ActivityCreateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * 活动处理库存RPC服务
 *
 * @author 张治保
 * date 2023/3/7
 */
public interface StorageActivityRpcService {

    /**
     * 活动sku库存预处理
     *
     * @param activityCreate 新建活动使用的库存
     */
    void activityCreate(@Valid ActivityCreateDTO activityCreate);

    /**
     * 活动库存归还
     *
     * @param activityCloses 活动关闭 归还库存参数列表
     */
    void activityClose(@Valid @Size(min = 1) Set<ActivityCloseKey> activityCloses);

}
