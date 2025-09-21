package com.medusa.gruul.carrier.pigeon.service.modules.oss.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.OssConstant;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.mp.service.IOssConfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jipeng
 * @since 2024/2/23
 */
@Slf4j
@Configuration
@EnableFileStorage
@RequiredArgsConstructor
public class OssMvcConfig implements WebMvcConfigurer, InitializingBean {

    private static final Lock LOCAL_OSS_PATH_LOCK = new ReentrantLock();
    /**
     * 本地存储路径
     */
    private static String localOssPath;
    private final IOssConfService ossConfService;

    /**
     * 获取本地存储路径
     * user.home路径  + "/gruul/oss"
     *
     * @return 本地存储路径
     */
    public static String localOssPath() {
        if (StrUtil.isNotEmpty(localOssPath)) {
            return localOssPath;
        }
        LOCAL_OSS_PATH_LOCK.lock();
        try {
            if (StrUtil.isNotEmpty(localOssPath)) {
                return localOssPath;
            }
            String userHome = System.getProperty("user.home");
            String fullPath = userHome + File.separator + "gruul" + File.separator + "oss";
            File file = FileUtil.mkdir(fullPath);
            localOssPath = file.getAbsolutePath();
        } finally {
            LOCAL_OSS_PATH_LOCK.unlock();
        }
        return localOssPath;
    }

    /**
     * 增加本地 oss 资源文件映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String pattern = "/" + OssConstant.LOCAL_OSS_REQUEST_PATH + "**";
        String resourceLocation = "file:" + localOssPath() + File.separator;
        log.debug("pattern: {}", pattern);

        log.debug("resourceLocation: {}", resourceLocation);
        registry.addResourceHandler(pattern)
                .addResourceLocations(resourceLocation);
    }

    /**
     * 将所有的 OSS 配置信息加载到 fileStorageService中
     */
    @Override
    public void afterPropertiesSet() {
        ossConfService.ossPlatformInit();
    }
}
