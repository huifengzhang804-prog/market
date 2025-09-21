package com.medusa.gruul.common.addon.supporter.sacnner;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.BeanFactory;

/**
 * 扫描器上下文
 *
 * @author 张治保
 * date 2022/9/16
 */
@Getter
@Setter
@Accessors(chain = true)
public class ScannerContext {

    /**
     * 是否是单体应用
     */
    private boolean isSingleApplication;

    /**
     * 当前服务名称
     */
    private String serviceName;

    /**
     * bean 工厂
     */
    private BeanFactory beanFactory;

    /**
     * bean 类加载器
     */
    private ClassLoader classLoader;
}
