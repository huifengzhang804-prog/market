package com.medusa.gruul.common.addon.model.handler;


import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 视图处理器抽象
 * 插件声明周期
 *
 * @author 张治保
 * date 2022/3/2
 */
public interface AddonHook extends InitializingBean, DisposableBean {
    /**
     * 插件安装
     */
    void install();

    /**
     * 插件卸载
     */
    void uninstall();


    /**
     * 初始化
     */
    @Override
    default void afterPropertiesSet() {
        this.install();
    }

    /**
     * 容器销毁
     */
    @Override
    default void destroy() {
        this.uninstall();
    }
}
