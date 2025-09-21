package com.medusa.gruul.order.service.modules.printer.feie.api.model.enums;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.annotation.JSONType;
import com.alibaba.fastjson2.reader.ObjectReader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;

/**
 * 飞鹅打印机状态
 *
 * @author 张治保
 * @since 2024/8/14
 */
@Getter
@JSONType(deserializer = FeiePrinterStatus.FeiePrinterStatusReader.class)
@RequiredArgsConstructor
public enum FeiePrinterStatus {
    /**
     * 打印机离线
     * 离线的判断是打印机与服务器失去联系超过2分钟。
     */
    OFFLINE(0, "离线"),

    /**
     * 在线，工作状态正常
     */
    ONLINE_OK(1, "在线，工作状态正常"),

    /**
     * 在线，工作状态不正常
     * 备注：异常一般是无纸，
     */
    ONLINE_NOT_OK(2, "在线，工作状态不正常"),
    ;

    private final int value1;

    private final String value2;

    public static class FeiePrinterStatusReader implements ObjectReader<FeiePrinterStatus> {
        @Override
        public FeiePrinterStatus readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
            if (jsonReader.isNull()) {
                return null;
            }
            Object read = jsonReader.read(Object.class);
            for (FeiePrinterStatus value : FeiePrinterStatus.values()) {
                if (((Integer) value.getValue1()).equals(read) || value.getValue2().equals(read)) {
                    return value;
                }
            }
            return null;
        }
    }

}
