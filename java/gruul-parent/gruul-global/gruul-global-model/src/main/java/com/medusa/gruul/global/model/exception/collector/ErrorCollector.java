package com.medusa.gruul.global.model.exception.collector;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * @since 2023/10/18
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorCollector {

    /**
     * 消息模板
     */
    private static final String DEFAULT_MSG_TEMPLATE = "%s：%s";

    private static final CharSequence DEFAULT_VALUES_SEPARATOR = "；";
    /**
     * 默认分隔符
     */
    private static final String VALUE_SEPARATOR = "、";

    /**
     * 默认的 key 转换器
     */
    private static final Function<Comparable<?>, ?> DEFAULT_KEY_MAPPER = Function.identity();

    /**
     * 默认的 value 转换器
     */
    private static final Function<FieldError, String> DEFAULT_VALUE_MAPPER = FieldError::toString;


    /**
     * 错误信息 收集结果
     */
    private final Map<Comparable<?>, List<FieldError>> errors = new TreeMap<>();

    /**
     * keyMapper 键转换器
     */
    private Function<Comparable<?>, ?> keyMapper = DEFAULT_KEY_MAPPER;

    /**
     * valueMapper 值转换器
     */
    private Function<FieldError, String> valueMapper = DEFAULT_VALUE_MAPPER;

    /**
     * 创建一个错误信息收集器
     *
     * @return 错误信息收集器
     */
    public static ErrorCollector create() {
        return new ErrorCollector();
    }

    /**
     * 自定义主键转换器
     *
     * @param keyMapper 主键转换器
     * @return this
     */
    public ErrorCollector keyMapper(Function<Comparable<?>, ?> keyMapper) {
        this.keyMapper = keyMapper;
        return this;
    }

    /**
     * 自定义错误信息转换器
     *
     * @param valueMapper 错误信息转换器
     * @return this
     */
    public ErrorCollector valueMapper(Function<FieldError, String> valueMapper) {
        this.valueMapper = valueMapper;
        return this;
    }

    /**
     * 创建一个当前行的错误信息收集器
     *
     * @param id   当前行的主键
     * @param task 业务逻辑
     * @param <T>  业务逻辑的返回值类型
     * @return 业务逻辑的返回值
     */
    public <T> T current(Comparable<?> id, Supplier<T> task) {
        CurrentErrors preCollector = CurrentErrorHolder.get();
        CurrentErrors current = new CurrentErrors(id);
        try {
            CurrentErrorHolder.set(current);
            return task.get();
        } finally {
            if (preCollector != null) {
                CurrentErrorHolder.set(preCollector);
            } else {
                CurrentErrorHolder.clear();
            }
            add(current);
        }
    }

    /**
     * 创建一个当前行的错误信息收集器
     *
     * @param id   当前行的主键
     * @param task 业务逻辑
     */
    public void current(Comparable<?> id, Runnable task) {
        current(id, () -> {
            task.run();
            return null;
        });
    }

    /**
     * 添加当前行的错误信息
     *
     * @param current 当前行的错误信息收集器
     */
    void add(CurrentErrors current) {
        if (CollUtil.isEmpty(current.errors())) {
            return;
        }
        List<FieldError> fieldErrors = errors.computeIfAbsent(current.id(), k -> new ArrayList<>());
        fieldErrors.addAll(current.errors());
        Collections.sort(fieldErrors);
    }

    /**
     * 获取所有已收集的错误信息
     *
     * @return 错误信息
     */
    public Map<Comparable<?>, List<FieldError>> errors() {
        return errors;
    }

    /**
     * 获取所有已收集的错误信息 转 list
     *
     * @return 错误信息
     */
    public List<String> toList() {
        return this.toList(keyMapper, valueMapper);
    }

    /**
     * 是否有错误
     *
     * @return 是否有错误
     */
    public boolean hasError() {
        return CollUtil.isNotEmpty(errors);
    }

    /**
     * 获取所有已收集的错误信息 转 String
     *
     * @return 错误信息
     */
    @Override
    public String toString() {
        return toString(DEFAULT_VALUES_SEPARATOR);
    }

    /**
     * 获取所有已收集的错误信息 转 String
     *
     * @param separator 错误信息的分隔符
     * @return 错误信息
     */
    public String toString(CharSequence separator) {
        return StrUtil.join(separator, toList());
    }

    /**
     * 获取所有已收集的错误信息 转 list
     *
     * @param keyMapper   主键转换器
     * @param valueMapper 错误信息转换器
     * @return 错误信息
     */
    private List<String> toList(Function<Comparable<?>, ?> keyMapper, Function<FieldError, String> valueMapper) {
        return errors.entrySet()
                .stream()
                .map(
                        entry -> {
                            Object apply = keyMapper.apply(entry.getKey());
                            String valuesStr = entry.getValue().stream().map(valueMapper).collect(Collectors.joining(VALUE_SEPARATOR));
                            return apply == null ? valuesStr : String.format(DEFAULT_MSG_TEMPLATE, apply, valuesStr);
                        }
                ).toList();
    }


    /**
     * 合并错误信息
     *
     * @param errors 错误信息
     * @return this
     */
    public ErrorCollector merge(Map<Comparable<?>, List<FieldError>> errors) {
        if (errors == null || errors.isEmpty()) {
            return this;
        }
        errors.forEach(
                (key, value) -> {
                    List<FieldError> fieldErrors = this.errors.computeIfAbsent(key, k -> new ArrayList<>());
                    fieldErrors.addAll(value);
                    Collections.sort(fieldErrors);
                }
        );
        return this;
    }

    /**
     * 合并错误信息
     *
     * @param errorCollector 错误信息收集器
     * @return this
     */
    public ErrorCollector merge(ErrorCollector errorCollector) {
        if (errorCollector == null) {
            return this;
        }
        return merge(errorCollector.errors);
    }

    /**
     * 如果有错误 则 抛出错误信息
     */
    public void throwIfError() {
        if (hasError()) {
            throw new GlobalException(toString());
        }
    }

}
