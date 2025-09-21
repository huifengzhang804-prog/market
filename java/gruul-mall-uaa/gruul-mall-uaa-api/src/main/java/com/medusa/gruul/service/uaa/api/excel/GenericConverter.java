package com.medusa.gruul.service.uaa.api.excel;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class GenericConverter implements Converter<Object> {

    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String value = cellData.getStringValue();
        Class<?> fieldType = contentProperty.getField().getType();
        // 根据字段类型执行相应的转换逻辑
        if (fieldType == String.class) {
            return value;
        } else if (fieldType == Integer.class) {
            return Integer.parseInt(value);
        } else if (fieldType == Long.class) {
            return Long.parseLong(value);
        } else if (fieldType.isEnum()) {
            Object enumValue = convertEnumValue(fieldType, value);
            if (enumValue == null) {
                throw new RuntimeException("wrong enum value");
            }
            return enumValue;
        }
        return null;
    }

    private Object convertEnumValue(Class<?> clazz, String value) {
        for (Object enumConstant : clazz.getEnumConstants()) {
            String desc = ReflectUtil.invoke(enumConstant, "getDesc");
            if (StrUtil.equals(desc, value)) {
                return enumConstant;
            }
        }
		return null;
    }

}
