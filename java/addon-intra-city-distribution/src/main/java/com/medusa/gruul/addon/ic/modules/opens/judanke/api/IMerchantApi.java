package com.medusa.gruul.addon.ic.modules.opens.judanke.api;

import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.addon.ic.modules.opens.judanke.JudankeResponse;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.merchant.BindMerchantResp;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.merchant.BindMerchantParam;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.merchant.SendVerifyCodeParam;

/**
 * @author 张治保
 * @since 2024/8/6
 */
public interface IMerchantApi extends IBaseJudankeApi {

    /**
     * 创建聚单客商户时，需要进行手机号验证
     *
     * @param param 发送验证码请求参数
     * @return 聚单客响应结果
     */
    default JudankeResponse<Void> sendVerifyCode(SendVerifyCodeParam param) {
        return request(param, new TypeReference<>() {
        });
    }


    /**
     * 创建聚单客商户可以进行发单
     * 额外说明
     * 1、创建虚拟号时，不需要验证码验证
     * 2、当真实使用，需要真实号码，可以使用换绑手机号接口更换虚拟号
     */
    default JudankeResponse<BindMerchantResp> bindMerchant(BindMerchantParam param) {
        return request(param, new TypeReference<>() {
        });
    }


    @Override
    default String apiPrefix() {
        return "/Merchant/";
    }
}
