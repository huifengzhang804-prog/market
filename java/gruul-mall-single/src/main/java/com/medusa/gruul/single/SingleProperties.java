package com.medusa.gruul.single;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * @author 张治保
 * date 2023/9/6
 */

@Data
@ConfigurationProperties(prefix = "gruul.single-config")
public class SingleProperties {

    /**
     * 使用的插件
     */
    private Set<String> addons = Set.of();

}
