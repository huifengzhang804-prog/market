package com.medusa.gruul.addon.ic.modules.opens.judanke;

import com.medusa.gruul.global.model.helper.request.IResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2024/8/6
 */
@Getter
@Setter
@ToString
public class JudankeResponse<T> implements IResponse<T> {

    /**
     * code	int	是
     * 响应状态码。0-成功，非0-失败下载并查看详情
     */
    private Integer code;

    /**
     * 返回结果说明
     */
    private String msg;

    /**
     * 本次请求唯一业务流水号
     */
    private String uid;

    /**
     * JSON格式响应数据
     */
    private T data;

    /**
     * 判断是否响应成功
     *
     * @return 是否响应成功
     */
    @Override
    public final boolean isSuccess() {
        return code != null && code == 0;
    }
}
