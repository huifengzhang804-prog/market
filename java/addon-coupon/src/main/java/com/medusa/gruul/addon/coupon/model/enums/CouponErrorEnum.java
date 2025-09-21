package com.medusa.gruul.addon.coupon.model.enums;


/**
 * @author jipeng
 * @since 2024/3/13
 */

import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 优惠券错误
 *
 * @author jipeng
 * @since 2024/3/13
 */
@Getter
@RequiredArgsConstructor
public enum CouponErrorEnum implements Error {

    CAN_NOT_DELETE(33310, "未开始,进行中的活动不能删除"),
    CAN_NOT_REMOVE(33311, "进行中的活动不能删除"),
    SHOP_NOT_MATCH(33312, "店铺id不匹配"),
    COUPON_STATE_ERROR(33313, "业务状态不正确"),

    /**
     * 临时EXCEL创建发生错误
     */
    TEMP_EXCEL_GENERATE_ERROR(33314, "临时EXCEL创建发生错误"),

    /**
     * EXCEL文件上传失败
     */
    EXCEL_UPLOADER_ERROR(33315, "EXCEL文件上传失败"),

    ;
    private final int code;
    private final String msgCode;

    @Override
    public int code() {
        return getCode();
    }

    @Override
    public String msg() {
        return getMsgCode();
    }
}
