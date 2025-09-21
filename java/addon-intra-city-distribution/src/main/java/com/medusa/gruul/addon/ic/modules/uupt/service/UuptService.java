package com.medusa.gruul.addon.ic.modules.uupt.service;

import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptConfig;
import com.medusa.gruul.addon.ic.modules.uupt.model.dto.UuptConfigDTO;
import com.medusa.gruul.addon.ic.modules.uupt.model.dto.UuptShopAuthDTO;
import com.medusa.gruul.addon.ic.modules.uupt.model.dto.UuptShopSmsDTO;
import com.medusa.gruul.addon.ic.modules.uupt.model.vo.UuptShopRechargeVO;
import com.medusa.gruul.addon.ic.modules.uupt.model.vo.UuptShopSmsVO;
import com.medusa.gruul.addon.ic.modules.uupt.model.vo.UuptStatusVO;

/**
 * @author 张治保
 * @since 2024/8/13
 */
public interface UuptService {

    /**
     * 保存 uupt配置
     *
     * @param param 配置参数
     */
    void saveOpenConfig(UuptConfigDTO param);

    /**
     * 获取 uupt开放平台配置
     *
     * @return uupt开放平台配置
     */
    UuptConfig getOpenConfig();


    /**
     * 查询 uu 跑腿开通状态与余额
     *
     * @param shopId 店铺 id
     * @return 开通状态
     */
    UuptStatusVO status(Long shopId);

    /**
     * 店铺发送短信验证码进行 uupt 授权
     *
     * @param param 手机号与图片验证码参数
     * @return 发送结果
     */
    UuptShopSmsVO sendSms(UuptShopSmsDTO param);

    /**
     * 店铺授权 开通 uupt
     *
     * @param shopId 店铺 id
     * @param param  手机号与短信验证码
     */
    void auth(Long shopId, UuptShopAuthDTO param);

    /**
     * 获取店铺 UU 跑腿余额充值链接
     *
     * @param shopId 店铺 id
     * @return 充值链接
     */
    UuptShopRechargeVO recharge(Long shopId);
}
