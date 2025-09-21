package com.medusa.gruul.order.api.model.wechat.logistics;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 小程序订单上传类型
 *
 * @author wufuzhong
 * time 2024/01/08 15:11
 * desc 小程序订单上传类型
 */
@Getter
@RequiredArgsConstructor
public enum OrderUploadType {
    /**
     * 发货信息录入接口
     */
    UPLOAD_SHIPPING_INFO(1),

    /**
     * 发货信息合单录入接口
     */
    UPLOAD_COMBINED_SHIPPING_INFO(2);

    @EnumValue
    private final Integer value;
}
