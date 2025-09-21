package com.medusa.gruul.addon.ic.modules.opens.uupt.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.medusa.gruul.addon.ic.modules.opens.global.IBaseApi;
import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptConfig;
import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptRequest;
import com.medusa.gruul.addon.ic.modules.opens.uupt.UuptResponse;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.global.model.helper.request.IRequest;
import com.medusa.gruul.global.model.helper.request.IResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author 张治保
 * @since 2024/8/10
 */
public interface IBaseUuptApi extends IBaseApi<UuptConfig> {

    Logger LOG = LoggerFactory.getLogger(IBaseUuptApi.class);

    @Override
    @SuppressWarnings("unchecked")
    default <Resp extends IResponse<Data>, Data> Resp request(@NotNull String api, @Nullable Object param, @Nullable TypeReference<Data> ref) {
        UuptConfig config = config();
        String openId = OpenId.LOCAL.get();
        String requestBody = JSON.toJSONString(UuptRequest.of(config.getAppKey(), openId, param));
        LOG.debug("--==>api:{}, requeestBody:{}", api, requestBody);
        String response = IRequest.INSTANCE.DEFAULT
                .post(
                        config.urlPrefix() + apiPrefix() + api,
                        Map.of(
                                "Content-Type", "application/json;charset=utf-8",
                                "X-App-Id", config.getAppid()
                        ),
                        requestBody
                );
        LOG.debug("--==>api:{}, response:{}", api, response);
        JSONObject jsonObject = JSON.parseObject(response);
        UuptResponse<Object> uuptResponse = jsonObject.to(UuptResponse.class);
        if (ref != null && ref.getType() != Object.class && ref.getType() != Void.class) {
            uuptResponse.setBody(FastJson2.convert(uuptResponse.getBody(), ref));
        }
        return (Resp) uuptResponse;
    }

    @Override
    default <Resp extends IResponse<Data>, Data> Resp request(@Nullable Object param, @Nullable TypeReference<Data> ref) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String api = stackTrace[2].getMethodName();
        if (api.startsWith("lambda$") && "withOpenId".equals(stackTrace[3].getMethodName())) {
            api = stackTrace[4].getMethodName();
        }
        return request(api,
                param,
                ref
        );
    }

    default <T> T withOpenId(String openId, Supplier<T> supplier) {
        OpenId.LOCAL.set(openId);
        try {
            return supplier.get();
        } finally {
            OpenId.LOCAL.remove();
        }
    }

    class OpenId {
        private static final ThreadLocal<String> LOCAL = new TransmittableThreadLocal<>();
    }
}
