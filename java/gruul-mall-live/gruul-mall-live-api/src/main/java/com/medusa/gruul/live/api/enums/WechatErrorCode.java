package com.medusa.gruul.live.api.enums;


import lombok.Getter;

/**
 * @author miskw
 * @date 2022/11/9
 */
@Getter
public enum WechatErrorCode {
    /**
     *
     */
    PUBLIC_CODE(1001, "微信通用CODE"),
    CURRENT_ANCHOR_WECHAT_NUMBER_NON_REAL_NAME(1002, "当前主播微信号没有在 “小程序直播“ 小程序实名认证"),
    SYSTEM_ERROR(-1, "微信小程序第三方系统繁忙"),
    ACCESS_TOKEN_ERROR(40001, "access_token校验失败"),
    PARAMETER_VERIFICATION_ERROR(200002, "参数错误"),
    NAME_LENGTH_ERROR(300002, "名称长度不符合规则"),
    PRICE_ERROR(300003, "价格输入不合规（如现价比原价大、传入价格非数字等）"),
    PRICE_NAME_ERROR(300004, "商品名称存在违规违法内容"),
    UPLOAD_IMAGE_ERROR(300006, "图片上传失败（如：mediaID过期）"),
    NOT_FOUND_URL(300007, "线上小程序版本不存在该链接"),
    AUDITING_NOT_DELETED(300014, "审核中，无法删除"),
    NOT_AUDITING_STATUS(300017, "非审核中状态，无法撤销"),
    AUDIT_FAILED(300021, "商品添加成功，审核失败"),
    GOODS_IMAGES_OVER(300018, "商品图片尺寸过大"),
    NOT_FOUND_ROOM(300022, "此房间号不存在"),
    LIVE_ROOM_ADD_GOODS_STATUS(300023, "房间状态 拦截(当前房间状态不允许此操作)"),
    NOT_FOUND_GOODS(300024, "商品不存在"),
    GOODS_APPROVAL_FAILED(300025, "商品审核未通过"),
    GOODS_ADD_FAIL(300027, "导入商品失败"),
    ROOM_NAME_ERROR(300028, "房间名称违规"),
    LIVE_ROOM_COVER_ERROR(300031, "直播间封面图不合规"),
    LIVE_ROOM_SHARE_ERROR(300032, "直播间分享图违规"),
    GOODS_ADD_LIMIT(300033, "添加商品超过直播间上限"),
    HOST_NICKNAME_ERROR(300034, "主播微信昵称长度不符合要求"),
    WECHAT_REAL_NAME_AUTHENTICATION_ERROR(300036, "主播微信号未实名认证,认证地址:https://res.wx.qq.com/op_res/9rSix1dhHfK4rR049JL0PHJ7TpOvkuZ3mE0z7Ou_Etvjf-w1J_jVX0rZqeStLfwh"),
    FEEDS_IMG_ERROR(300037, "购物直播频道封面图不合规"),
    APPLETS_CUSTOMER_SERVICE(300038, "小程序管理后台尚未配置客服"),
    SUB_ANCHOR_WECHAT_ERROR(300039, "主播副号微信号不合法"),
    SPECIAL_CHARACTERS_ERROR(300040, "名称含有非限定字符（含有特殊字符）"),
    CREATE_WECHAT_ERROR(300041, "创建者微信号不合法"),
    WECHAT_NON_CONFORMANCE(400001, "微信号不合规"),
    REAL_NAME_AUTHENTICATION(400002, "https://res.wx.qq.com/op_res/9rSix1dhHfK4rR049JL0PHJ7TpOvkuZ3mE0z7Ou_Etvjf-w1J_jVX0rZqeStLfwh"),
    OVER_LIMIT_ROLE(400003, "添加角色达到上限（管理员10个，运营者500个，主播500个）"),
    USER_ALREADY_MANAGER_ROLE(400004, "重复添加角色"),
    existence_Non_begin_Live_broadcast_room(400005, "该主播存在未开播的直播间"),
    CREATE_ANCHOR_REPEAT(400006, "不能重复添加，该主播已添加过");
    /**
     * 错误码
     */
    private final int code;
    /**
     * 描述
     */
    private final String describe;

    /**
     *
     */


    WechatErrorCode(int code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    public static String getDescribe(int code) {
        for (WechatErrorCode value : WechatErrorCode.values()) {
            if (value.getCode() == code) {
                return value.getDescribe();
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }
}
