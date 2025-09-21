package com.medusa.gruul.global.model.helper.request;

import java.util.Map;

/**
 * @author 张治保
 * @since 2024/8/10
 */
public interface IRequest {

    String post(String url, Map<String, String> headers, String body);

    class INSTANCE {
        public static final IRequest DEFAULT = new Request(10 * 1000);
    }
}

