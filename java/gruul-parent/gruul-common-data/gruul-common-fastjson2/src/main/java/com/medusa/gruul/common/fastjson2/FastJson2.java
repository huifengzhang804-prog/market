package com.medusa.gruul.common.fastjson2;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson2.*;
import com.alibaba.fastjson2.util.TypeUtils;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;

/**
 * fastjson2通用配置与门面
 *
 * @author 张治保
 * date 2023/4/6
 */
public class FastJson2 {

    //时间格式化
    public static final String NO_SECONDS_TIME_PATTEN = "HH:mm";
    public static final DateTimeFormatter NO_SECONDS_TIME_FORMATTER = DateTimeFormatter.ofPattern(NO_SECONDS_TIME_PATTEN);
    public static final String TIME_PATTEN = DatePattern.NORM_TIME_PATTERN;
    public static final DateTimeFormatter TIME_FORMATTER = DatePattern.NORM_TIME_FORMATTER;
    //日期格式化
    public static final String DATE_PATTEN = DatePattern.NORM_DATE_PATTERN;
    public static final DateTimeFormatter DATE_FORMATTER = DatePattern.NORM_DATE_FORMATTER;
    //日期时间格式化
    public static final String DATETIME_PATTEN = DatePattern.NORM_DATETIME_PATTERN;
    public static final DateTimeFormatter DATETIME_FORMATTER = DatePattern.NORM_DATETIME_FORMATTER;
    public static final String NO_SECONDS_DATETIME_PATTEN = DatePattern.NORM_DATETIME_MINUTE_PATTERN;
    public static final DateTimeFormatter NO_SECONDS_DATETIME_FORMATTER = DatePattern.NORM_DATETIME_MINUTE_FORMATTER;
    /**
     * 反序列化配置
     */
    private static final JSONReader.Feature[] READ_FEATURE = {
            JSONReader.Feature.IgnoreSetNullValue,
            //JSONReader.Feature.TrimString,
            JSONReader.Feature.ErrorOnEnumNotMatch

    };
    /**
     * 序列化配置
     */
    private static final JSONWriter.Feature[] WRITE_FEATURE = {
            JSONWriter.Feature.WriteEnumsUsingName,
            JSONWriter.Feature.WriteBigDecimalAsPlain,
            //JSONWriter.Feature.WriteLongAsString,
            //JSONWriter.Feature.BrowserSecure

    };

    /**
     * 获取反序列化配置
     *
     * @return 反序列化配置
     */
    public static JSONReader.Feature[] readFeature() {
        return READ_FEATURE;
    }

    /**
     * 获取序列化配置
     *
     * @return 序列化配置
     */
    public static JSONWriter.Feature[] writeFeature() {
        return WRITE_FEATURE;
    }


    /**
     * 将对象转换为指定类型
     *
     * @param value 值
     * @param type  类型
     * @param <T>   类型
     * @return 转换后的对象
     */
    public static <T> T convert(Object value, Class<T> type) {
        return convert(value, type, FastJson2.readFeature());
    }

    /**
     * 将对象转换为指定类型
     *
     * @param value    值
     * @param type     类型
     * @param features 配置
     * @param <T>      类型
     * @return 转换后的对象
     */
    public static <T> T convert(Object value, Class<T> type, JSONReader.Feature... features) {
        //判断目标type类型
        if (Map.class.isAssignableFrom(type)) {
            return JSONObject.from(value).to(type, features);
        }
        if (Collection.class.isAssignableFrom(type)) {
            return JSONArray.from(value).to(type);
        }
        return JSON.to(type, value);
    }


    /**
     * 将对象转换为指定类型
     *
     * @param value 值
     * @param type  类型
     * @param <T>   类型
     * @return 转换后的对象
     */
    public static <T> T convert(Object value, Type type) {
        return TypeUtils.cast(value, type);
    }

    /**
     * 将对象转换为指定类型
     *
     * @param value     值
     * @param reference 类型
     * @param <T>       类型
     * @return 转换后的对象
     */
    public static <T> T convert(Object value, TypeReference<T> reference) {
        return convert(value, reference, FastJson2.readFeature());
    }

    /**
     * 将对象转换为指定类型
     *
     * @param value     值
     * @param reference 类型
     * @param features  配置
     * @param <T>       类型
     * @return 转换后的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object value, TypeReference<T> reference, JSONReader.Feature... features) {
        if (value == null) {
            return null;
        }
        if (value instanceof Map<?, ?> mapValue) {
            return new JSONObject(mapValue).to(reference, FastJson2.readFeature());
        }
        if (value instanceof String strValue) {
            return JSON.parseObject(strValue, reference, FastJson2.readFeature());
        }
        Class<? super T> rawType = reference.getRawType();
        if (Map.class.isAssignableFrom(rawType)) {
            return JSONObject.from(value).to(reference, features);
        }
        if (Collection.class.isAssignableFrom(rawType)) {
            return reference.to(JSONArray.from(value));
        }
        if (rawType.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return JSON.parseObject(JSON.toJSONString(value), reference, features);
    }

}
