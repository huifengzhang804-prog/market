package com.medusa.gruul.global.i18n;

import org.springframework.lang.NonNull;

import java.util.Set;

/**
 * 国际化路径配置加载器
 *
 * @author 张治保
 * date 2023/6/15
 */
public interface I18NPropertiesLoader {

    /**
     * 国际化配置路径 多个
     *
     * @return 路径集合
     */
    @NonNull
    default Set<String> paths() {
        return Set.of();
    }


    /**
     * 国际化配置路径 单个
     *
     * @return 路径
     */
    default String path() {
        return null;
    }
}
