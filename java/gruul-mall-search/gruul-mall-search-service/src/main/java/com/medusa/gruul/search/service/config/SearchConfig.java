package com.medusa.gruul.search.service.config;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.medusa.gruul.global.config.helper.ExecutorHelper;
import com.medusa.gruul.search.service.properties.SearchConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.core.biz.EntityInfo;
import org.dromara.easyes.core.kernel.BaseEsMapper;
import org.dromara.easyes.core.toolkit.EntityInfoHelper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author WuDi
 * date 2022/10/31
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SearchConfig {

    /**
     * 异步执行线程池
     */
    @Bean
    public Executor searchCompletableTaskExecutor(SearchConfigurationProperties searchConfigurationProperties) {
        return TtlExecutors.getTtlExecutor(
                ExecutorHelper.toTaskExecutor(searchConfigurationProperties.getThreadPool())
        );
    }

    /**
     * 自动创建索引（easy es 设置为手动模式）
     * todo 注意没加分布式锁，分布式多机部署下会出现问题
     * todo 应避免多机同时启动运行
     *
     * @param mappers es mapper
     * @return CommandLineRunner
     */
    @Bean
    public CommandLineRunner searchCommandLineRunner(List<BaseEsMapper<?>> mappers) {
        return args -> mappers.forEach(
                mapper -> {
                    EntityInfo entityInfo = EntityInfoHelper.getEntityInfo(mapper.getEntityClass());
                    String indexName = entityInfo.getIndexName();
                    Boolean existed = mapper.existsIndex(indexName);
                    if (BooleanUtil.isTrue(existed)) {
                        return;
                    }
                    mapper.createIndex();
                    log.debug("已自动创建索引：{}",indexName);
                }
        );
    }

}
