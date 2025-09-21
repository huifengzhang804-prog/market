package com.medusa.gruul.addon.platform.model.enums;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlatformError implements Error {

    /**
     * 参数无效
     */
    PARAMETERS_INVALID(100001, "parameters.invalid"),


    /**
     * 模版名称已经存在
     */
    TEMPLATE_NAME_ALREADY_EXIST(100002, "template.name.already.exist"),

    /**
     * 页面不存在
     */
    PAGE_NOT_EXIST(100003, "page.not.exist"),
    /**
     * 模版名称已经存在
     */
    TEMPLATE_CREATION_FAILED(100004, "template.creation.failed"),

    /**
     * 无效的页面类型
     */
    PAGE_TYPE_INVALID(100005, "page.type.invalid"),

    /**
     * 模板不存在
     */
    TEMPLATE_NOT_EXIST(100006, "template.not.exist"),

    /**
     * 模板状态不能修改
     */
    TEMPLATE_STATUS_CAN_NOT_MODIFY(100007, "template.status.can.not.modify"),

    /**
     * 模板存在页面引用
     */
    TEMPLATE_EXIST_PAGE_REFERENCES(100008, "Template.exists.page.references"),

    /**
     * 删除模板失败
     */
    FAILED_TO_DELETE_TEMPLATE(100009, "failed.to.delete.template"),

    /**
     * 更新模板失败
     */
    FAILED_TO_UPDATE_TEMPLATE(100010, "failed.to.update.template"),

    /**
     * 页面名称不能重复by templateType+
     */
    PAGE_NAME_ALREADY_EXIST(100011, "page.name.can.not.be.repeated"),

    /**
     * 创建页面失败
     */
    FAILED_TO_CREATE_PAGE(100012, "failed.to.create.page"),


    /**
     * 更新页面失败
     */
    FAILED_TO_UPDATE_PAGE(100013, "failed.to.update.page"),

    /**
     * 删除页面失败
     */
    REFERENCED_BY_TEMPLATE(100014, "referenced.by.template"),

    /**
     * 删除页面失败
     */
    FAILED_TO_DELETE_PAGE(100015, "failed.to.delete.page"),

    FAILED_TO_CHANGE_TEMPLATE_STATUS(100016, "failed.to.change.template.status"),

    TEMPLATE_CAN_NOT_DELETE(100017, "template.can.not.delete"),
    TEMPLATE_NOT_ENABLED_ON_PLATFORM(100018, "template.not.enabled.on.the.platform"),

    TEMPLATE_NAME_LENGTH_TOO_LONG(100019, "template.name.length.too.long"),


    PAGE_NAME_LENGTH_TOO_LONG(100020, "template.name.length.too.long");


    private final int code;
    private final String msgCode;

    @Override
    public int code() {
        return getCode();
    }

    @Override
    public String msg() {
        return I18N.msg(getMsgCode());
    }
}
