package com.medusa.gruul.addon.ic.modules.ic.util;

/**
 * @author 张治保
 * @since 2024/11/15
 */
public interface ICHelper {

    /**
     * 获取格式化后的取件码
     * eg. #01、#03
     *
     * @param pickupCode 原始取件码
     * @return 格式化后的取件码
     */
    static String pickupCodeFormat(Long pickupCode) {
        if (pickupCode == null) {
            return null;
        }
        return String.format("#%02d", pickupCode);
    }

}
