package com.medusa.gruul.carrier.pigeon.service.modules.oss.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.carrier.pigeon.service.model.vo.ConfVO;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.config.StorageConf;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.dto.OssConfigDto;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.enums.StorageType;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.mp.entity.OssConf;

import java.util.List;
import java.util.Map;

/**
 * @author xiaoq
 * 系统配置
 */
public interface IOssConfService extends IService<OssConf> {
    /**
     * 保存或更新oss配置 当前配置会作为默认使用的 OSS 平台
     *
     * @param editOssConfigDto oss配置信息
     */
    void editOssConf(OssConfigDto editOssConfigDto);

    /**
     * 获取指定类型的存储配置
     *
     * @param type 查询类型
     * @return 对应平台的配置
     */
    StorageConf ossConfig(StorageType type);

    /**
     * 获取所有的OSS配置
     *
     * @return 各平台及其对应的 oss 配置信息
     */
    Map<StorageType, ? extends StorageConf> allConf();

    /**
     * 存储平台的初始化
     */
    void ossPlatformInit();

    /**
     * 获取当前正在使用种的平台
     *
     * @return 正在使用的平台
     */
    StorageType usingConfig();

    /**
     * 查询OSS配置列表
     * @return
     */
    List<OssConf> ossList();

    /**
     * 清除指定服务商的配置
     * @param type
     */
    void removeConfig(StorageType type);

    /**
     * 设置使用的服务商
     * @param type
     */
    void using(StorageType type);
}
