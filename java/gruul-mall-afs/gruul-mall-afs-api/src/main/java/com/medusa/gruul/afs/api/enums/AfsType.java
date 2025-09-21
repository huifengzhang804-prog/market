package com.medusa.gruul.afs.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/3
 */
@Getter
@RequiredArgsConstructor
public enum AfsType {

    /**
     * 仅退款
     */
    REFUND(1, "TK", AfsStatus.REFUND_REQUEST),

    /**
     * 退货退款
     */
    RETURN_REFUND(2, "TH", AfsStatus.RETURN_REFUND_REQUEST);


    @EnumValue
    private final Integer value;
    /**
     * 对应的单号前缀
     */
    private final String noPrefix;
    /**
     * 对应的售后状态
     */
    private final AfsStatus afsStatus;


}
