package com.medusa.gruul.addon.seckill;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 秒杀服务启动类
 *
 * @author 张治保
 * @since 2024/5/28
 */
@SpringBootApplication
@EnableDubbo(scanBasePackages = "com.medusa.gruul.addon.seckill.addon.impl")
public class AddonSeckillApplication implements I18NPropertiesLoader {

    public static void main(String[] args) {
        SpringApplication.run(AddonSeckillApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/seckill";
    }
}
