package com.medusa.gruul.common.security.resource.tool;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @author 张治保
 * @since 2023/11/2
 */
public interface SecurityChainConfig {

    /**
     * config HttpSecurity
     *
     * @param http HttpSecurity
     */
    void config(HttpSecurity http);

}
