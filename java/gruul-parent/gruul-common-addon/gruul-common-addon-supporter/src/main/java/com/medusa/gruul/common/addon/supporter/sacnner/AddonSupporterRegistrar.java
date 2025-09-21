package com.medusa.gruul.common.addon.supporter.sacnner;


import com.medusa.gruul.common.addon.model.constant.AddonConst;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.common.addon.supporter.helper.AddonSupporterHelper;
import com.medusa.gruul.global.i18n.I18N;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * 扫描插件接口 并注册未动态代理对象  参考 mybatis AutoConfiguredMapperScannerRegistrar
 *
 * @author 张治保
 */
@Slf4j
public class AddonSupporterRegistrar implements BeanFactoryAware, EnvironmentAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware {
    private static final String SERVICE_SINGLE_KEY_IN_ENVIRONMENT = "gruul.single";
    private static final String SERVICE_NAME_KEY_IN_ENVIRONMENT = "spring.application.name";
    private final ScannerContext scannerContext = new ScannerContext();
    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {
        log.debug(AddonConst.ADDON_LOG_TEMPLATE, I18N.msg("addon.supporter.scanning"), "@AddonSupporter");
        ClassPathAddonSupporterScanner scanner = new ClassPathAddonSupporterScanner(registry, scannerContext);
        try {
            if (this.resourceLoader != null) {
                scanner.setResourceLoader(this.resourceLoader);
            }
            List<String> packages = AutoConfigurationPackages.get(scannerContext.getBeanFactory());
            scanner.addIncludeFilter(new AnnotationTypeFilter(AddonSupporter.class));
            scanner.doScan(packages.toArray(new String[0]));
        } catch (IllegalStateException ex) {
            log.debug(AddonConst.ADDON_LOG_TEMPLATE, I18N.msg("addon.supporter.scanned.nothing"), I18N.msg("addon.supporter.not.worked"), ex);
        }
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.scannerContext.setBeanFactory(beanFactory);
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanClassLoader(@NonNull ClassLoader classLoader) {
        this.scannerContext.setClassLoader(classLoader);
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        boolean isSingle = environment.getProperty(SERVICE_SINGLE_KEY_IN_ENVIRONMENT, Boolean.class, false);
        this.scannerContext.setSingleApplication(isSingle);
        this.scannerContext.setServiceName(environment.getProperty(SERVICE_NAME_KEY_IN_ENVIRONMENT));
        AddonSupporterHelper.setContext(this.scannerContext);
    }
}