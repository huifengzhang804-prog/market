package com.medusa.gruul.global.model.helper.request;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/8/10
 */
public interface IResponse<T> extends Serializable {

    /**
     * 是否响应成功
     *
     * @return 是否响应成功
     */
    boolean isSuccess();

}
