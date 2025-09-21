package com.medusa.gruul.global.model.helper;

import java.util.Set;

/**
 * @author 张治保
 * @since 2023/12/7
 */
public class ShutdownHook {

    /**
     * 用于存储shutdown 回调钩子集合
     */
    private static final Set<Runnable> HOOKS = new java.util.concurrent.CopyOnWriteArraySet<>();

    /**
     * addShutdownHook
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Runnable hook : HOOKS) {
                hook.run();
            }
        }));
    }

    /**
     * 添加钩子 谨慎使用 确保任务可以快速结束 避免阻塞关闭操作
     *
     * @param hook 钩子
     */
    public static void add(Runnable hook) {
        HOOKS.add(hook);
    }

    /**
     * 移除钩子
     *
     * @param hook 钩子
     */
    static void remove(Runnable hook) {
        HOOKS.remove(hook);
    }

}
