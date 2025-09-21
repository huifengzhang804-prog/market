package com.medusa.gruul.global.model.o;

import java.io.Serializable;

/**
 * 基础的数据传输对象
 *
 * @author 张治保
 * date 2022/5/25
 */
public interface BaseDTO extends Serializable {

    /**
     * 检查校验参数
     */
    default void validParam() {
    }


    /**
     * 模糊查询字符%拼接
     *
     * @param value 原始字符
     * @return 拼接后的字符
     */
    default String like(String value) {
        return "'%" + value + "%'";
    }

}
