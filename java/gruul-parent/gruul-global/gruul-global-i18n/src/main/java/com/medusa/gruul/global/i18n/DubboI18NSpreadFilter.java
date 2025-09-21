package com.medusa.gruul.global.i18n;

import cn.hutool.core.util.StrUtil;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * dubbo 传递国际化信息
 *
 * @author 张治保
 * date 2023/6/26
 */
@Activate(group = {CommonConstants.CONSUMER, CommonConstants.PROVIDER})
public class DubboI18NSpreadFilter implements Filter {

    private static final String LANGUAGE_KEY = "lang";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //如果是消费者端 则传递 语言信息
        if (RpcContext.getServerContext().isConsumerSide()) {
            //消费者
            RpcContext.getClientAttachment().setAttachment(LANGUAGE_KEY, I18N.getLocale().toLanguageTag());
            return invoker.invoke(invocation);
        }
        //否则是服务端 则解析语言信息
        String language = RpcContext.getServerAttachment().getAttachment(LANGUAGE_KEY);
        if (StrUtil.isEmpty(language)) {
            return invoker.invoke(invocation);
        }
        LocaleContextHolder.setLocale(Locale.forLanguageTag(language), true);
        try {
            return invoker.invoke(invocation);
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }
    }
}
