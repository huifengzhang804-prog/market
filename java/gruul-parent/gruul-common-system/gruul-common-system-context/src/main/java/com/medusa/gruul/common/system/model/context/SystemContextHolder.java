package com.medusa.gruul.common.system.model.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.medusa.gruul.common.system.model.model.Systems;

/**
 * 客户端ip地址上下文
 * @author 张治保
 * date 2021/12/15
 */
public class SystemContextHolder {

    private static final ThreadLocal<Systems> LOCAL = new TransmittableThreadLocal<>();

    public static void set(Systems systems){
        LOCAL.set(systems);
    }

    public static Systems get() {
        return LOCAL.get();
    }


    public static void clear(){
        LOCAL.remove();
    }
}
