package com.medusa.gruul.common.web;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import com.alibaba.fastjson2.support.spring6.webservlet.view.FastJsonJsonView;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.fastjson2.filter.DesensitizeValueFilter;
import com.medusa.gruul.common.web.converter.CodecGenericConverter;
import com.medusa.gruul.common.web.converter.EnumConverterFactory;
import com.medusa.gruul.common.web.parameter.RequestBodyParamResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

/**
 * @author 张治保
 * date 2023/4/6
 */
@EnableWebMvc
@RequiredArgsConstructor
public class FastJson2MvcAdapter implements WebMvcConfigurer {

    private final FastJsonConfig config = new FastJsonConfig();

    {
        config.setDateFormat(FastJson2.DATETIME_PATTEN);
        config.setReaderFeatures(ArrayUtil.append(FastJson2.readFeature(), JSONReader.Feature.TrimString));
        config.setWriterFeatures(ArrayUtil.append(FastJson2.writeFeature(), JSONWriter.Feature.BrowserCompatible));
        config.setWriterFilters(DesensitizeValueFilter.INSTANCE);
    }

    @Override
    public void configureMessageConverters(@NonNull List<HttpMessageConverter<?>> converters) {
        //是否已存在 默认编码为 utf-8 字符串消息转换器
        boolean hashUtf8StringConverter = hashUtf8StringConverter(converters);

        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        converter.setFastJsonConfig(config);
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        converter.setSupportedMediaTypes(
                List.of(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"))
        );
        converters.add(0, converter);
        if (hashUtf8StringConverter) {
            return;
        }
        //如果没有 默认编码为 utf-8 字符串消息转换器 手动添加一个
        StringHttpMessageConverter plainTextStringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        plainTextStringConverter.setSupportedMediaTypes(List.of(MediaType.TEXT_PLAIN));
        converters.add(1, plainTextStringConverter);
    }

    /**
     * 是否包含 utf8 格式的 converter 并移除 MappingJackson2HttpMessageConverter
     *
     * @param converters 所有的 converter
     * @return 是否包含 utf8 格式的 converter
     */
    private boolean hashUtf8StringConverter(List<HttpMessageConverter<?>> converters) {
        Iterator<HttpMessageConverter<?>> iterator = converters.iterator();
        //是否已存在 默认编码为 utf-8 字符串消息转换器
        boolean hashUTF8StringConverter = false;
        while (iterator.hasNext()) {
            HttpMessageConverter<?> converter = iterator.next();
            if (
                    converter instanceof StringHttpMessageConverter strConvert
                            && StandardCharsets.UTF_8 == strConvert.getDefaultCharset()
                            && strConvert.getSupportedMediaTypes().contains(MediaType.TEXT_PLAIN)
            ) {
                hashUTF8StringConverter = true;
            }
            //如果是 jackson 的 convert 则直接移除掉 
            if ("org.springframework.http.converter.json.MappingJackson2HttpMessageConverter".equals(converter.getClass().getName())) {
                iterator.remove();
            }
        }
        return hashUTF8StringConverter;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        FastJsonJsonView fastJsonJsonView = new FastJsonJsonView();
        fastJsonJsonView.setFastJsonConfig(config);
        registry.enableContentNegotiation(fastJsonJsonView);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(EnumConverterFactory.INSTANCE);
        registry.addConverter(new CodecGenericConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new RequestBodyParamResolver());
    }
}

