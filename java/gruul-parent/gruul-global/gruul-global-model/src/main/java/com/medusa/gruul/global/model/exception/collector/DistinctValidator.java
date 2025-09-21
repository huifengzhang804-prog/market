package com.medusa.gruul.global.model.exception.collector;

import cn.hutool.core.collection.CollUtil;

import java.util.*;
import java.util.function.Function;

/**
 * 重复数据校验工具
 *
 * @author 张治保
 * @since 2023/10/27
 */
public interface DistinctValidator {

    String ROW_NUMBER_TEMPLATE = "第%s行";
    String DOT = "、";

    /**
     * 数据集合字段重复性校验
     *
     * @param list      可迭代数据集合
     * @param field     获取对应的字段值
     * @param fieldDesc 字段描述
     * @param <T>       值类型
     * @return 异常结果收集
     */
    static <T> ErrorCollector valid(List<T> list, Function<T, Comparable<?>> field, String fieldDesc) {
        if (list == null || list.isEmpty()) {
            return ErrorCollector.create();
        }
        Map<Number, T> rowDataMap = new HashMap<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            rowDataMap.put(i + 1, list.get(i));
        }
        return DistinctValidator.valid(rowDataMap, field, fieldDesc);
    }

    /**
     * 数据集合字段重复性校验
     *
     * @param rowDataMap 数据集合 key 行号 value 数据
     * @param field      获取对应的字段值
     * @param fieldDesc  字段描述
     * @param <T>        值类型
     * @return 异常结果收集
     */
    static <T> ErrorCollector valid(Map<? extends Number, T> rowDataMap, Function<T, Comparable<?>> field, String fieldDesc) {
        if (CollUtil.isEmpty(rowDataMap)) {
            return ErrorCollector.create();
        }
        Map<Comparable<?>, Set<Long>> distinctData = new HashMap<>(rowDataMap.size());
        rowDataMap.forEach(
                (key, value) -> {
                    Comparable<?> filedValue = field.apply(value);
                    if (filedValue == null) {
                        return;
                    }
                    distinctData.computeIfAbsent(filedValue, k -> new HashSet<>())
                            .add(key.longValue());
                }
        );
        ErrorCollector collector = ErrorCollector.create()
                .keyMapper(id -> null)
                .valueMapper(fieldError -> fieldError.getFiled() + "：" + fieldError.getDesc());
        distinctData.forEach(
                (key, value) -> {
                    if (value.size() <= 1) {
                        return;
                    }
                    StringBuilder msgBuilder = new StringBuilder();
                    value.stream().sorted()
                            .forEach(id -> msgBuilder.append(String.format(ROW_NUMBER_TEMPLATE, id)).append(DOT));
                    msgBuilder.deleteCharAt(msgBuilder.length() - 1);
                    collector.current(
                            key,
                            () -> CurrentErrors.add(msgBuilder.toString(), fieldDesc + "【" + key + "】重复")
                    );
                }
        );
        return collector;
    }


}
