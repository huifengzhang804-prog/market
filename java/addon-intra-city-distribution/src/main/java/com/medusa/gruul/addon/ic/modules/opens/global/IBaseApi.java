package com.medusa.gruul.addon.ic.modules.opens.global;

import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.global.model.helper.request.IResponse;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author 张治保
 * @since 2024/8/10
 */
public interface IBaseApi<C> {

    /**
     * 聚单客 api 前缀
     *
     * @return 聚单客 api 前缀
     */
    String apiPrefix();

    /**
     * 聚单客配置
     *
     * @return 配置详情
     */
    C config();

    /**
     * 发起聚单客请求
     *
     * @param api   请求的 api 后缀
     * @param param 请求参数
     * @param ref   响应数据泛型
     * @return 响应结果
     */
    <Resp extends IResponse<Data>, Data> Resp request(@Nonnull String api, @Nullable Object param, @Nullable TypeReference<Data> ref);

    /**
     * 发起聚单客请求 此方式会获取上一不的方法名作为 api 后缀
     *
     * @param param 请求参数
     * @return 响应结果
     */
    default <Resp extends IResponse<Data>, Data> Resp request(@Nullable Object param, @Nullable TypeReference<Data> ref) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String api = stackTrace[2].getMethodName();
        return request(api,
                param,
                ref
        );
    }
}
