package com.medusa.gruul.global.model.exception.collector;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张治保
 * @since 2023/10/18
 */
@RequiredArgsConstructor
public class CurrentErrors {

    /**
     * 当前行的主键
     */
    private final Comparable<?> id;
    /**
     * 当前行的错误数据
     */
    private final List<FieldError> errors = new ArrayList<>();

    /**
     * 添加错误信息
     *
     * @param desc 错误描述信息
     */
    public static void add(String desc) {
        add(null, desc);
    }

    /**
     * 添加错误信息 静态方法 方便调用 无需传递当前行的错误信息收集器
     *
     * @param filed 字段信息
     * @param desc  错误描述信息
     */
    public static void add(String filed, String desc) {
        add(new FieldError().setFiled(filed).setDesc(desc));
    }

    /**
     * 添加错误信息 静态方法 方便调用 无需传递当前行的错误信息收集器
     *
     * @param fieldError 字段与描述信息
     */
    public static void add(FieldError fieldError) {
        CurrentErrors currentErrors = CurrentErrorHolder.get();
        if (currentErrors == null) {
            throw new RuntimeException("当前行错误信息收集器未初始化,当前 task 必须使用 ErrorCollector.current 方法执行");
        }
        currentErrors.errors.add(fieldError);
    }

    /**
     * 判断是否有错误
     *
     * @return 是否有错误
     */
    public static boolean hasError() {
        CurrentErrors currentErrors = CurrentErrorHolder.get();
        if (currentErrors == null) {
            throw new RuntimeException("当前行错误信息收集器未初始化,当前 task 必须使用 ErrorCollector.current 方法执行");
        }
        return CollUtil.isNotEmpty(currentErrors.errors);
    }

    /**
     * errors getter
     *
     * @return errors
     */
    List<FieldError> errors() {
        return errors;
    }

    /**
     * id getter
     *
     * @return id
     */
    Comparable<?> id() {
        return id;
    }


}
