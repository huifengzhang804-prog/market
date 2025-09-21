package com.medusa.gruul.common.mp.config;

import com.medusa.gruul.common.mp.IManualTransaction;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

/**
 * @author 张治保
 * date 2022/8/8
 */
@Configuration
public class TransactionalConfig {

    @Bean
    public CommandLineRunner manualTransactionLineRunner(PlatformTransactionManager platformTransactionManager, TransactionDefinition transactionDefinition) {
        return args -> IManualTransaction.setManage(platformTransactionManager, transactionDefinition);
    }
}
