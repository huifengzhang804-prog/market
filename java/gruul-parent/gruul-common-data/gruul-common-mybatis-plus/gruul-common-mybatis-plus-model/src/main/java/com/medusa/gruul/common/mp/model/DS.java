package com.medusa.gruul.common.mp.model;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;

import java.util.function.Supplier;

/**
 * 手动切换数据源
 *
 * @author 张治保
 * @since 2024/5/14
 */
public interface DS {


    /**
     * shardingSphere数据源名称
     */
    String SHARDING_SPHERE_DS = "shardingSphere";

    /**
     * 动态数据源切换至 sharding jdbc
     *
     * @param task 切换执行的任务
     */
    static void sharding(Runnable task) {
        to(SHARDING_SPHERE_DS, task);
    }

    /**
     * 动态数据源切换至 sharding jdbc
     *
     * @param task 切换执行的任务
     * @param <T>  任务的返回值类型
     * @return 返回结果
     */
    static <T> T sharding(Supplier<T> task) {
        return to(SHARDING_SPHERE_DS, task);
    }

    /**
     * 动态数据源切换
     *
     * @param dsName 数据源名称
     * @param task   切换执行的任务
     */
    static void to(String dsName, Runnable task) {
        DynamicDataSourceContextHolder.push(dsName);
        try {
            task.run();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 动态数据源切换
     *
     * @param dsName 数据源名称
     * @param task   切换执行的任务
     * @param <T>    任务的返回值类型
     * @return 返回结果
     */
    static <T> T to(String dsName, Supplier<T> task) {
        DynamicDataSourceContextHolder.push(dsName);
        try {
            return task.get();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

}
