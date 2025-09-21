package com.medusa.gruul.carrier.pigeon.service.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 配置返回体
 *
 * @param <U> 当前使用的配置
 * @param <C> 配置数据类型
 * @author 张治保
 * @since 2024/3/16
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ConfVO<U, C> {
    /**
     * 当前使用中的配置类型
     */
    private U using;

    /**
     * 当前查询的配置数据
     */
    private C conf;
}
