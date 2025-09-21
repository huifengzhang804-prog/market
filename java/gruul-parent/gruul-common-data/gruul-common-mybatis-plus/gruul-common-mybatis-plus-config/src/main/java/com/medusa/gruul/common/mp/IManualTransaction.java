package com.medusa.gruul.common.mp;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 手动事务操作
 *
 * @author 张治保
 * date 2022/8/8
 */
public class IManualTransaction {
    private static PlatformTransactionManager platformTransactionManager;
    private static TransactionDefinition transactionDefinition;

    /**
     * 设置事务管理器
     *
     * @param platformTransactionManager 事务管理器
     * @param transactionDefinition      事务定义信息
     */
    public static void setManage(PlatformTransactionManager platformTransactionManager, TransactionDefinition transactionDefinition) {
        IManualTransaction.platformTransactionManager = platformTransactionManager;
        IManualTransaction.transactionDefinition = transactionDefinition;

    }

    /**
     * 当前事务提交之后 会执行的任务
     */
    public static void afterCommit(Runnable... tasks) {
        //如果没有活动事务则直接执行
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            for (Runnable runnable : tasks) {
                runnable.run();
            }
            return;
        }
        //否则事务提交之后执行
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        for (Runnable runnable : tasks) {
                            runnable.run();
                        }
                    }
                }
        );
    }


    /**
     * 手动开启一个事务
     *
     * @param task 事务操作
     */
    public static void todo(Runnable task) {
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
        boolean hashError = false;
        try {
            task.run();
            platformTransactionManager.commit(transactionStatus);
        } catch (Exception exception) {
            hashError = true;
            throw exception;
        } finally {
            if (hashError && !transactionStatus.isCompleted()) {
                platformTransactionManager.rollback(transactionStatus);
            }
        }
    }

}
