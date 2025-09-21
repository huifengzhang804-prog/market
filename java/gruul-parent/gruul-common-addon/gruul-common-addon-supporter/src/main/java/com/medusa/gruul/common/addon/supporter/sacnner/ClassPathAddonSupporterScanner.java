package com.medusa.gruul.common.addon.supporter.sacnner;

import com.medusa.gruul.common.addon.model.constant.AddonConst;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.common.addon.supporter.proxy.AddonSupporterFactoryBean;
import com.medusa.gruul.global.i18n.I18N;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * 参考 ClassPathMapperScanner
 *
 * @author 张治保
 * date 2022/9/16
 */
@Slf4j
public class ClassPathAddonSupporterScanner extends ClassPathBeanDefinitionScanner {

    private final ScannerContext scannerContext;

    public ClassPathAddonSupporterScanner(BeanDefinitionRegistry registry, ScannerContext scannerContext) {
        super(registry);
        this.scannerContext = scannerContext;
    }

    /**
     * 调用将搜索并注册所有候选人的父搜索。然后对注册的对象进行后处理以将它们设置为 AddonSupporterFactoryBean
     */
    @Override
    @NonNull
    public Set<BeanDefinitionHolder> doScan(@NonNull String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug(AddonConst.ADDON_LOG_TEMPLATE, I18N.msg("addon.supporter.scanned.nothing"), Arrays.toString(basePackages));
            }
        } else {
            this.processBeanDefinitions(beanDefinitions);
        }
        return beanDefinitions;
    }

    /**
     * 处理bean定义信息
     *
     * @param beanDefinitionHolders 扫描到的bean定义信息
     */
    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        beanDefinitionHolders.forEach(
                holder -> {
                    AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) holder.getBeanDefinition();
                    //添加构造方法参数
                    ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
                    String originalClassName = beanDefinition.getBeanClassName();
                    constructorArgumentValues.addGenericArgumentValue(Objects.requireNonNull(originalClassName));
                    //添加属性参数
                    beanDefinition.getPropertyValues().add("scannerContext", this.scannerContext);
                    beanDefinition.setBeanClass(AddonSupporterFactoryBean.class);
                    log.debug(AddonConst.ADDON_LOG_TEMPLATE, I18N.msg("addon.supporter.working"), originalClassName);
                }
        );
        log.debug(AddonConst.ADDON_LOG_TEMPLATE, I18N.msg("addon.supporter.all.working"), "");
    }

    @Override
    protected boolean isCandidateComponent(@NonNull AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isInterface() && !metadata.isAnnotation() && metadata.isAnnotated(AddonSupporter.class.getCanonicalName());
    }
}
