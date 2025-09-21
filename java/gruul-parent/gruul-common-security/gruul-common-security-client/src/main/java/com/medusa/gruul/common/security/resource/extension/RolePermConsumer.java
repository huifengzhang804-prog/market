package com.medusa.gruul.common.security.resource.extension;

import com.medusa.gruul.common.security.model.bean.SecureUser;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 角色权限消费器 用于 and、 or、 customer
 *
 * @author 张治保
 * date 2023/4/12
 */
public final class RolePermConsumer implements IRolePermMatcher<RolePermConsumer>, Consumer<IRolePermMatcher<?>> {

    /**
     * 任务队列
     */
    private final Queue<Consumer<IRolePermMatcher<?>>> taskQueue = new LinkedList<>();

    @Override
    public void accept(IRolePermMatcher matcher) {
        while (!taskQueue.isEmpty()) {
            taskQueue.poll().accept(matcher);
        }
    }

    @Override
    public RolePermConsumer anonymous() {
        taskQueue.add(IRolePermMatcher::anonymous);
        return this;
    }

    @Override
    public <T> RolePermConsumer neq(Function<SecureUser<?>, Set<T>> property, T condition) {
        taskQueue.add(matcher -> matcher.neq(property, condition));
        return this;
    }

    @Override
    public <T> RolePermConsumer eq(Function<SecureUser<?>, Set<T>> property, T condition) {
        taskQueue.add(matcher -> matcher.eq(property, condition));
        return this;
    }

    @Override
    @SafeVarargs
    public final <T> RolePermConsumer any(Function<SecureUser<?>, Set<T>> property, T... conditions) {
        taskQueue.add(matcher -> matcher.any(property, conditions));
        return this;
    }

    @Override
    @SafeVarargs
    public final <T> RolePermConsumer all(Function<SecureUser<?>, Set<T>> property, T... conditions) {
        taskQueue.add(matcher -> matcher.all(property, conditions));
        return this;
    }

    @Override
    public RolePermConsumer and(Consumer<IRolePermMatcher<?>> and) {
        taskQueue.add(matcher -> matcher.and(and));
        return this;
    }

    @Override
    public RolePermConsumer or(Consumer<IRolePermMatcher<?>> or) {
        taskQueue.add(matcher -> matcher.or(or));
        return this;
    }

    @Override
    public RolePermConsumer custom(Function<IRolePermMatcher<?>, Boolean> custom) {
        taskQueue.add(matcher -> matcher.custom(custom));
        return this;
    }
}
