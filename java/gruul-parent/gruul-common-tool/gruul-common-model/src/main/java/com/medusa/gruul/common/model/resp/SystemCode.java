package com.medusa.gruul.common.model.resp;

import com.medusa.gruul.global.i18n.I18N;
import lombok.AllArgsConstructor;

/**
 * 系统内置code
 *
 * @author L.cm
 */
@AllArgsConstructor
public enum SystemCode implements IResultCode {
    /**
     * 系统未知异常
     */
    FAILURE(SystemCode.FAILURE_CODE, "model.unknown"),
    /**
     * 缺少必要的请求参数
     */
    PARAM_MISS(SystemCode.PARAM_MISS_CODE, "model.param.missed"),
    /**
     * 请求参数类型错误
     */
    PARAM_TYPE_ERROR(SystemCode.PARAM_TYPE_ERROR_CODE, "model.param.wrong.type"),
    /**
     * 请求参数绑定错误
     */
    PARAM_BIND_ERROR(SystemCode.PARAM_BIND_ERROR_CODE, "model.param.bind.error"),
    /**
     * 参数校验失败
     */
    PARAM_VALID_ERROR(SystemCode.PARAM_VALID_ERROR_CODE, "model.param.invalid"),
    /**
     * 404 没找到请求
     */
    NOT_FOUND(SystemCode.NOT_FOUND_CODE, "model.query.not.found"),
    /**
     * 消息不能读取
     */
    MSG_NOT_READABLE(SystemCode.MSG_NOT_READABLE_CODE, "model.parameter.read.error"),

    //-------------------------------------------------------------//
    /**
     * 数据不存在
     */
    DATA_NOT_EXIST(SystemCode.DATA_NOT_EXIST_CODE, "model.query.not.found"),
    /**
     * 数据已存在
     */
    DATA_EXISTED(SystemCode.DATA_EXISTED_CODE, "model.data.existed"),
    /**
     * 数据添加失败
     */
    DATA_ADD_FAILED(SystemCode.DATA_ADD_FAILED_CODE, "model.data.add.failed"),
    /**
     * 数据更新失败
     */
    DATA_UPDATE_FAILED(SystemCode.DATA_UPDATE_FAILED_CODE, "model.data.update.failed"),
    /**
     * 数据删除失败
     */
    DATA_DELETE_FAILED(SystemCode.DATA_DELETE_FAILED_CODE, "model.data.delete.failed"),

    /**
     * 接口限流
     */
    SENTINEL_FLOW_BLOCK(SystemCode.SENTINEL_FLOW_BLOCK_CODE, "model.system.busy"),

    /**
     * 服务降级
     */
    SENTINEL_DEGRADE_BLOCK(SystemCode.SENTINEL_DEGRADE_BLOCK_CODE, "model.system.busy"),

    /**
     * 热点参数限流
     */
    SENTINEL_PARAM_FLOW_BLOCK(SystemCode.SENTINEL_PARAM_FLOW_BLOCK_CODE, "model.system.busy"),

    /**
     * 触发系统保护规则
     */
    SENTINEL_SYSTEM_BLOCK(SystemCode.SENTINEL_SYSTEM_BLOCK_CODE, "model.system.busy"),

    /**
     * 授权规则不通过
     */
    SENTINEL_AUTHORITY_BLOCK(SystemCode.SENTINEL_AUTHORITY_BLOCK_CODE, "model.system.busy"),

    /**
     * 系统繁忙
     */
    SYSTEM_BUSY(SystemCode.SYSTEM_BUSY_CODE, "model.system.busy"),

    /**
     * 短信模板不存在
     */
    SMS_TEMPLATE_NOT_EXISTS(SystemCode.DATA_NOT_EXIST_CODE, "sms.template.not.exists"),

    /**
     * 短信模板更新失败
     */
    SMS_TEMPLATE_UPDATE_FAILED(SystemCode.DATA_UPDATE_FAILED_CODE, "sms.template.update.failed"),

    /**
     * 短信签名添加失败
     */
    SMS_SIGN_ADD_FAILED(SystemCode.DATA_ADD_FAILED_CODE, "sms.sign.add.failed"),

    /**
     * 短信模板添加失败
     */
    SMS_TEMPLATE_ADD_FAILED(SystemCode.DATA_ADD_FAILED_CODE, "sms.template.add.failed"),

    /**
     * 时间范围错误
     */
    TIME_RANGE_ERROR(SystemCode.TIME_RANGE_ERROR_CODE,"time.range.error");

    /**
     * 微信第三方平台异常 code
     */
    public static final int WX_PLATEFROM_EXCEPTION = 101000;


    /**
     * 通用 异常 code
     */
    public static final int FAILURE_CODE = 400;
    public static final int SUCCESS_CODE = 200;
    public static final int UNAUTHORIZED_CODE = 401;
    public static final int PARAM_MISS_CODE = 100000;
    public static final int PARAM_TYPE_ERROR_CODE = 100001;
    public static final int PARAM_BIND_ERROR_CODE = 100002;
    public static final int PARAM_VALID_ERROR_CODE = 100003;
    public static final int NOT_FOUND_CODE = 100004;
    public static final int MSG_NOT_READABLE_CODE = 100005;
    public static final int METHOD_NOT_SUPPORTED_CODE = 100006;
    public static final int MEDIA_TYPE_NOT_SUPPORTED_CODE = 100007;
    public static final int MEDIA_TYPE_NOT_ACCEPT_CODE = 100008;
    public static final int REQ_REJECT_CODE = 100009;
    public static final int SYSTEM_BUSY_CODE = 100010;
    public static final int TIME_RANGE_ERROR_CODE = 100011;

    /**
     * 通用数据层 code
     */
    public static final int DATA_NOT_EXIST_CODE = 100100;
    public static final int DATA_EXISTED_CODE = 100101;
    public static final int DATA_ADD_FAILED_CODE = 100102;
    public static final int DATA_UPDATE_FAILED_CODE = 100103;
    public static final int DATA_DELETE_FAILED_CODE = 100104;

    /**
     * 商品已经售罄
     */
    public static final int ITEM_SOLD_OUT_CODE = 200001;

    /**
     * 网关sentinel block code
     * 300001 ～ 300005
     */
    public static final int SENTINEL_FLOW_BLOCK_CODE = 300001;
    public static final int SENTINEL_DEGRADE_BLOCK_CODE = 300002;
    public static final int SENTINEL_PARAM_FLOW_BLOCK_CODE = 300003;
    public static final int SENTINEL_SYSTEM_BLOCK_CODE = 300004;
    public static final int SENTINEL_AUTHORITY_BLOCK_CODE = 300005;


    /**
     * code编码
     */
    final int code;
    /**
     * 中文信息描述
     */
    final String msgCode;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return I18N.msg(msgCode);
    }

}
