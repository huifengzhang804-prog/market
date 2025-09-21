package com.medusa.gruul.addon.member;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 张治保
 * date 2022/11/2
 */
@SpringBootApplication
@EnableDubbo(scanBasePackages = "com.medusa.gruul.addon.member.addon.impl")
public class AddonMemberApplication implements I18NPropertiesLoader {
    public static void main(String[] args) {
        SpringApplication.run(AddonMemberApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/member";
    }
}
