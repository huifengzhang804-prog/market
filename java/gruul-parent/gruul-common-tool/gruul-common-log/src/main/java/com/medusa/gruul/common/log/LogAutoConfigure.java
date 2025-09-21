package com.medusa.gruul.common.log;

import com.medusa.gruul.common.log.aspect.LogAdvisor;
import com.medusa.gruul.common.log.aspect.LogInterceptor;
import com.medusa.gruul.common.log.properties.LogProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张治保
 * date 2022/2/15
 */
@EnableConfigurationProperties(LogProperties.class)
@Configuration
public class LogAutoConfigure {

    @Bean
    public LogAdvisor logAdvisor(LogProperties logProperties){
        return new LogAdvisor(new LogInterceptor(logProperties));
    }
}
