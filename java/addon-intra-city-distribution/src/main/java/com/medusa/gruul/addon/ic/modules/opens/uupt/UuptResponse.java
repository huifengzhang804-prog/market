package com.medusa.gruul.addon.ic.modules.opens.uupt;

import com.medusa.gruul.global.model.helper.request.IResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/10
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UuptResponse<T> implements IResponse<T> {

    /**
     * code int 响应状态码：1-代表成功
     */
    private Integer code;

    /**
     * 和响应状态码一致
     */
    private String state;

    /**
     * body Object 响应数据
     */
    private T body;

    /**
     * 描述信息
     */
    private String msg;

    /**
     * ?
     */
    private String total;

    @Override
    public final boolean isSuccess() {
        return 1 == code;
    }
}
