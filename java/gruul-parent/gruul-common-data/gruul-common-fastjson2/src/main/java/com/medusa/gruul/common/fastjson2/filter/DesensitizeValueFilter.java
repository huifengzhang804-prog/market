package com.medusa.gruul.common.fastjson2.filter;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.filter.BeanContext;
import com.alibaba.fastjson2.filter.ContextValueFilter;
import com.alibaba.fastjson2.filter.Filter;
import com.medusa.gruul.common.fastjson2.annotation.Desensitize;

/**
 * 数据脱敏拦截器
 *
 * @author 张治保`
 * @since 2023/11/27
 */
public class DesensitizeValueFilter implements ContextValueFilter {


    public static final Filter INSTANCE = new DesensitizeValueFilter();

    /**
     * 脱敏
     *
     * @param annotation 脱敏注解
     * @param value      原始值
     * @return 脱敏后的值
     */
    public static String hide(Desensitize annotation, String value) {
        int length = value.length();
        //如果设置了开始和结束位置
        int start, end;
        if ((start = annotation.start()) != -1 && (end = annotation.end()) != -1) {
            return StrUtil.hide(value, start, length - end);
        }
        //如果设置了正则表达式和模板
        String regx, template;
        if (StrUtil.isNotEmpty(regx = annotation.regx()) && StrUtil.isNotEmpty(template = annotation.template())) {
            return ReUtil.replaceAll(value, regx, template);
        }
        //否则使用 脱敏工具脱敏
        return DesensitizedUtil.desensitized(value, annotation.type());
    }

    @Override
    public Object process(BeanContext context, Object object, String name, Object value) {
        if (!(value instanceof String)) {
            return value;
        }
        Desensitize annotation = context.getAnnotation(Desensitize.class);
        if (annotation == null) {
            return value;
        }
        return DesensitizeValueFilter.hide(annotation, value.toString());
    }
}
