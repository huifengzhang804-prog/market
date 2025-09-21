package com.medusa.gruul.global.model.helper;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * @author 张治保
 * date 2022/6/13
 */
@Slf4j
public class CompletableTask {

    private CompletableTask() {
    }

    /**
     * 获取异步执行结果 或 抛出异常
     *
     * @param completableFuture 异步执行任务
     * @param <T>               执行结果泛型
     */
    public static <T> T getOrThrowException(CompletableFuture<T> completableFuture) {
        try {
            return completableFuture.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException("asynchronous execution error", e);
        } catch (InterruptedException e) {
            log.error("interrupted exception", e);
            throw new RuntimeException("asynchronous execution error InterruptedException", e);
        }
    }

    /**
     * 渲染所有的异步任务至一个任务
     *
     * @param executor 异步任务执行器
     * @param tasks    任务列表
     * @return 合并后的任务
     */

    public static CompletableFuture<Void> allOf(Executor executor, Runnable... tasks) {
        int length = tasks.length;
        CompletableFuture<?>[] futures = new CompletableFuture[length];
        for (int i = 0; i < length; i++) {
            futures[i] = CompletableFuture.runAsync(tasks[i], executor)
                    .whenComplete(
                            (value, throwable) -> {
                                if (throwable == null) {
                                    return;
                                }
                                if (log.isErrorEnabled()) {
                                    log.error("CompletableFuture Exception", throwable);
                                }
                            }
                    );
        }
        return CompletableFuture.allOf(futures);
    }

    /**
     * all of
     *
     * @param tasks 任务列表
     * @return CompletableFuture
     */
    public static CompletableFuture<Void> allOf(CompletableFuture<?>... tasks) {
        for (CompletableFuture<?> task : tasks) {
            task.whenComplete(
                    (value, throwable) -> {
                        if (throwable == null) {
                            return;
                        }
                        if (log.isErrorEnabled()) {
                            log.error("CompletableFuture Exception", throwable);
                        }
                    }
            );
        }
        return CompletableFuture.allOf(tasks);
    }


}
