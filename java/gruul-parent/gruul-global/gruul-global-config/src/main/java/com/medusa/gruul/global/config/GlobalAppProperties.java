package com.medusa.gruul.global.config;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.global.model.enums.Mode;
import com.medusa.gruul.global.model.o.ThreadPoolProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;


/**
 * @author 张治保
 * date 2022/3/23
 */
@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "gruul")
public class GlobalAppProperties implements EnvironmentAware, InitializingBean {


    private Environment environment;

    /**
     * 应用服务名
     */
    private String name;
    /**
     * 应用程序版本号
     */
    private String version = "1.0";

    /**
     * 基础请求 url
     * <a href="https://test.bgniao.cn/api">例子</a>
     */
    private String baseUrl;

    /**
     * 是否是单体应用
     */
    private boolean single = false;

    /**
     * 业务运行模式 默认B2B2C
     */
    private Mode mode = Mode.B2B2C;


    /**
     * 线程池配置
     */
    @NestedConfigurationProperty
    private ThreadPoolProperties threadPool = new ThreadPoolProperties();


    @Override
    public void afterPropertiesSet() {
        if (StrUtil.isEmpty(this.name)) {
            this.name = environment.resolvePlaceholders("${spring.application.name}");
        }
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }


    /**
     * @param path api路径
     * @return 完整url
     */
    public String fullUrl(String path) {
        return Global.concatUrl(this.baseUrl, path);
    }
}

