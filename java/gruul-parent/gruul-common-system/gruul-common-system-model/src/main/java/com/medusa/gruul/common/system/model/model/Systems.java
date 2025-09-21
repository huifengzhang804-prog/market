package com.medusa.gruul.common.system.model.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2022/2/16
 */
@Getter
@Setter
@Accessors(chain = true)
public class Systems {

    /**
     * 客户端ip地址
     */
    private String ip;
    /**
     * 客户端设备id
     */
    private String deviceId;
    /**
     * 客户端类型
     */
    private ClientType clientType;
    /**
     * 客户端运行平台
     */
    private Platform platform;
    /**
     * 请求版本号
     */
    private String version;
    /**
     * 平台服务商id
     */
    private Long platformId;
    /**
     * 店铺id
     */
    private Long shopId;


}
