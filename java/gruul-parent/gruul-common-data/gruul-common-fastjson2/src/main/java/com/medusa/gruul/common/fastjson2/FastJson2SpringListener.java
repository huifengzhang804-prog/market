package com.medusa.gruul.common.fastjson2;

import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.common.fastjson2.rw.ReaderWriter;
import com.medusa.gruul.common.spring.listener.functions.GruulSpringListener;
import org.springframework.boot.ConfigurableBootstrapContext;

/**
 * @author 张治保
 * date 2023/7/17
 */
public class FastJson2SpringListener extends GruulSpringListener {
    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        //全局 date format
        JSON.configReaderDateFormat(FastJson2.DATETIME_PATTEN);
        JSON.configWriterDateFormat(FastJson2.DATETIME_PATTEN);
        //duration format
        for (ReaderWriter value : ReaderWriter.values()) {
            Class<?> type = value.READER_WRITER.codec().encodeClass();
            JSON.register(type, value.READER_WRITER.reader());
            JSON.register(type, value.READER_WRITER.writer());
        }
    }
}
