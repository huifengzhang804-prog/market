package com.medusa.gruul.addon.team;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 团购插件
 *
 * @author 张治保
 * date 2023/3/1
 */
@SpringBootApplication
@EnableDubbo(scanBasePackages = "com.medusa.gruul.addon.team.addon.impl")
public class AddonTeamApplication implements I18NPropertiesLoader {
    public static void main(String[] args) {
        SpringApplication.run(AddonTeamApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/team";
    }
}
