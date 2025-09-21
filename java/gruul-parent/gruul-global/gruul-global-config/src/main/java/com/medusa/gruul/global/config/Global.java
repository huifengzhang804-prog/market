package com.medusa.gruul.global.config;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;

/**
 * @author 张治保
 * @since 2024/6/6
 */
public interface Global {

    /**
     * 全局任务线程池 bean name
     */
    String GLOBAL_TASK_EXECUTOR = "globalTaskExecutor";

    /**
     * 全局线程池 bean name
     */
    String GLOBAL_EXECUTOR = "globalExecutor";


    /**
     * 拼接url
     *
     * @param urls url
     * @return url
     */
    static String concatUrl(String... urls) {
        char slash = '/';
        StringBuilder sb = new StringBuilder();
        for (String url : urls) {
            if (StrUtil.isBlank(url)) {
                continue;
            }
            url = url.trim();
            if (url.equals(StrPool.SLASH)) {
                continue;
            }
            if (sb.isEmpty()) {
                sb.append(url);
                continue;
            }
            boolean lastIsSlash = sb.charAt(sb.length() - 1) == slash;
            boolean urlFirstSlash = url.charAt(0) == slash;
            // 两边都有斜杠时，去掉一个斜杠
            if (lastIsSlash && urlFirstSlash) {
                sb.append(url, 1, url.length());
                continue;
            }
            // 两边都没有斜杠时，添加一个斜杠
            if (!lastIsSlash && !urlFirstSlash) {
                sb.append(slash)
                        .append(url);
                continue;
            }
            // 其他情况下直接拼接
            sb.append(url);
        }
        return sb.toString();
    }
}
