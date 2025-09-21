package com.medusa.gruul.addon.ic.modules.opens.judanke.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.addon.ic.modules.opens.global.IBaseApi;
import com.medusa.gruul.addon.ic.modules.opens.judanke.JudankeConfig;
import com.medusa.gruul.addon.ic.modules.opens.judanke.JudankeRequest;
import com.medusa.gruul.addon.ic.modules.opens.judanke.JudankeResponse;
import com.medusa.gruul.global.model.helper.request.IRequest;
import com.medusa.gruul.global.model.helper.request.IResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * 聚单客 api
 *
 * @author 张治保
 * @since 2024/8/6
 */
public interface IBaseJudankeApi extends IBaseApi<JudankeConfig> {


    /**
     * 发起聚单客请求
     *
     * @param api   请求的 api 后缀
     * @param param 请求参数
     * @param ref   响应数据泛型
     * @return 响应结果
     */
    @Override
    @SuppressWarnings("unchecked")
    default <Resp extends IResponse<Data>, Data> Resp request(@NotNull String api, @Nullable Object param, @Nullable TypeReference<Data> ref) {
        JudankeConfig config = config();
        String url = config.getDomain() + apiPrefix() + api;
        String response = IRequest.INSTANCE
                .DEFAULT
                .post(
                        url,
                        Map.of(
                                "Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"
                        ),
                        JSON.toJSONString(JudankeRequest.of(config.getUserId(), config.getApiKey(), api, param))
                );
        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(response);
        } catch (Exception e) {
            throw new RuntimeException(" (url=" + url + ") response content invalid(" + response + ").", e);
        }
        JudankeResponse<Data> result = new JudankeResponse<>();
        result.setCode(jsonObject.getIntValue("code"));
        result.setMsg(jsonObject.getString("msg"));
        result.setUid(jsonObject.getString("uid"));
        String respData = jsonObject.getString("data");
        if (
                ref == null
                        || ref.getType() == String.class
                        || ref.getType() == Object.class
                        || ref.getType() == Void.class
        ) {
            result.setData((Data) respData);
        } else if (respData == null || respData.isEmpty() || "{}".equals(respData) || "false".equals(respData)) {
            result.setData(null);
        } else {
            result.setData(
                    JSON.parseObject(
                            respData,
                            ref,
                            JSONReader.Feature.SupportSmartMatch
                    )
            );
        }
        return (Resp) result;
    }

}
