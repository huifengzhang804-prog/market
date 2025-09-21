package com.medusa.gruul.service.uaa.service.service;

import java.util.List;

/**
 *
 *
 * @author xiaoq
 * @Description 门店用户服务层
 * @date 2023-03-09 18:39
 */
public interface StoreUserService {
    /**
     * 检测门店用户信息
     *
     * @param mobiles 门店用户手机号
     */
    void checkStoreUserByMobile(List<String> mobiles);
}
