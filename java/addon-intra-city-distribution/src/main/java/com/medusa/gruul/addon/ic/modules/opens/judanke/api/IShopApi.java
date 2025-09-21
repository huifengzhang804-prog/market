package com.medusa.gruul.addon.ic.modules.opens.judanke.api;

import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.shop.CreateParam;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.shop.CreateResp;
import com.medusa.gruul.addon.ic.modules.opens.judanke.JudankeResponse;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.shop.ModifyParam;

/**
 * @author 张治保
 * @since 2024/8/6
 */
public interface IShopApi extends IBaseJudankeApi {

    /**
     * 修改编辑店铺信息
     *
     * @param param 修改店铺信息参数
     * @return 修改结果
     */
    default JudankeResponse<Void> modify(ModifyParam param) {
        return request("modify", param, null);
    }

    /**
     * 创建发单店铺
     * 商家发单寄件地址基于发单店铺进行发单
     *
     * @param param 创建店铺参数
     * @return 店铺创建结果
     */
    default JudankeResponse<CreateResp> create(CreateParam param) {
        return request(param, new TypeReference<>() {
        });
    }


    @Override
    default String apiPrefix() {
        return "/Shop/";
    }
}
