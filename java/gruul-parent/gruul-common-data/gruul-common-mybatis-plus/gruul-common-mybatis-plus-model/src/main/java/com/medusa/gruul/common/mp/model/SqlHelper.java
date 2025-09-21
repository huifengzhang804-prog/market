package com.medusa.gruul.common.mp.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;


/**
 * sql 工具了
 *
 * @author 张治保
 * date 2022/9/23
 */
public interface SqlHelper {

    /**
     * 格式化 占位符
     */
    String PLACEHOLDER = "{}";

    /**
     * json set template
     */
    String JSON_OPERATION_TEMPLATE = "'$.{}',{}";

    /**
     * json set json template
     */
    String JSON_OPERATION_JSON_TEMPLATE = "'$.{}',JSON_EXTRACT({}, '$')";

    /**
     * limit
     */
    String SQL_LIMIT = "LIMIT {},{}";

    /**
     * limit 1
     */
    String SQL_LIMIT_1 = "LIMIT 1";

    /**
     * limit n
     */
    String SQL_LIMIT_N = "LIMIT {}";

    /**
     * JSON_SET
     */
    String JSON_SET_TEMPLATE = "{}=JSON_SET({},{})";


    /**
     * IN sql template
     */
    String IN_SQL_TEMPLATE = "{} in ({})";

    /**
     * limit sql
     *
     * @param size 条数
     * @return 格式化后的 sql
     */
    static String limit(long size) {
        return StrUtil.format(SQL_LIMIT_N, size);
    }

    /**
     * limit sql
     *
     * @param current 偏移量
     * @param size    条数
     * @return 格式化后的 sql
     */
    static String limit(long current, long size) {
        return StrUtil.format(SQL_LIMIT, (current - 1) * size, size);
    }

    /**
     * 生成 json set sql
     * 例子: times=JSON_SET(times，'$.a',aValue,'$.b',bValue,'$.c',cValue)
     *
     * @param column      列名
     * @param fieldValues json 字段名与字段值 映射关系
     * @return 格式化后的 sql
     * @
     */
    static String renderJsonSetSql(String column, Tuple... fieldValues) {
        return StrUtil.format(JSON_SET_TEMPLATE, column, column, jsonFieldWithValues(fieldValues));
    }


    /**
     * 生成 in sql
     *
     * @param column 列名
     * @param values 值
     * @param <T>    类型
     * @return 格式化后的 sql
     */
    static <T> String inSql(String column, Collection<T> values) {
        return StrUtil.format(IN_SQL_TEMPLATE, column, StrUtil.join(StrPool.COMMA, values));
    }

    /**
     * 生成 in sql
     *
     * @param columns 列名
     * @param values  值
     * @param <T>     类型
     * @return 格式化后的 sql
     */
    static <T> String inSql(List<String> columns, Collection<T> values) {
        return inSql(columns, values, (Function<T, ? extends Serializable>) null);
    }

    /**
     * 生成 in sql
     *
     * @param columns      列名
     * @param values       值
     * @param valueMappers 值映射函数
     * @param <T>          类型
     * @return 格式化后的 sql
     */
    static <T> String inSql(List<String> columns, Collection<T> values, List<Function<T, ? extends Serializable>> valueMappers) {
        if (CollUtil.isEmpty(columns)) {
            return inSql(columns, values, (Function<T, ? extends Serializable>) null);
        }
        if (valueMappers.size() == 1) {
            return inSql(columns, values, valueMappers.get(0));
        }
        return inSql(
                columns,
                values,
                val -> "(" +
                        StrUtil.join(
                                StrPool.COMMA,
                                valueMappers.stream()
                                        .map(valueMapper -> valueMapper.apply(val))
                                        .toList()
                        ) +
                        ")"
        );
    }

    /**
     * 生成 in sql
     *
     * @param columns     列名
     * @param values      值
     * @param valueMapper 值映射函数
     * @param <T>         类型
     * @return 格式化后的 sql
     */
    static <T> String inSql(List<String> columns, Collection<T> values, Function<T, ? extends Serializable> valueMapper) {
        return StrUtil.format(
                IN_SQL_TEMPLATE,
                // 列名
                columns.size() == 1 ? columns.get(0) : "(" + StrUtil.join(StrPool.COMMA, columns) + ")",
                // 值
                StrUtil.join(StrPool.COMMA,
                        valueMapper != null
                                ? values.stream()
                                .map(valueMapper)
                                .toList()
                                : values
                )
        );
    }


    /**
     * 拼接所有字段
     * 例子:  '$.a',aValue,'$.b',bValue,'$.c',cValue
     *
     * @param fieldValues json 字段名与字段值 列表
     * @return 所有json
     */
    static String jsonFieldWithValues(Tuple... fieldValues) {
        int length = fieldValues.length;
        if (length == 1) {
            return jsonFieldWithValue(fieldValues[0]);
        }
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < length; index++) {
            if (index != 0) {
                sb.append(StrPool.COMMA);
            }
            sb.append(jsonFieldWithValue(fieldValues[index]));
        }
        return sb.toString();
    }

    /**
     * 单个字段拼接
     * 例子: '$.a',aValue
     *
     * @param fieldValue json 字段名与字段值 列表
     * @return 所有json
     */
    static String jsonFieldWithValue(Tuple fieldValue) {
        String template;
        Tuple2<?, ?> tuple2;
        if (fieldValue instanceof Tuple2<?, ?> tuple2Instance) {
            tuple2 = tuple2Instance;
            template = JSON_OPERATION_TEMPLATE;
        } else {
            Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) fieldValue;
            tuple2 = Tuple.of(tuple3._1(), tuple3._2());
            template = JSON_OPERATION_JSON_TEMPLATE;
        }
        Object value = tuple2._2();
        return StrUtil.format(template, tuple2._1(), value instanceof CharSequence ? StrUtil.format("'{}'", value) : value);
    }
}