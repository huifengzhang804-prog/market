package com.medusa.gruul.shop.service.model.enums;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2023/6/26
 */
@Getter
@RequiredArgsConstructor
public enum ShopError implements Error {

    /**
     * 已存在同名店铺
     */
    SHOP_NAME_EXISTED(80000, "shop.name.existed"),

    /**
     * 装修控件不存在
     */
    SHOP_DECORATE_COMPONENT_NOT_EXIST(80001, "shop.decorate.component.not.exist"),

    /**
     * 装修数据不存在，无法使用
     */
    SHOP_DECORATE_DATA_NOT_EXIST(80002, "shop.decorate.data.not.exist"),

    /**
     * 已存在相同的地址，无法重复添加
     */
    SHOP_LOGISTICS_ADDRESS_EXISTED(80003, "shop.logistics.address.existed"),

    /**
     * 不能删除默认地址
     */
    SHOP_DEFAULT_LOGISTICS_ADDRESS_NOT_DEL(80004, "shop.default.logistics.address.not.del"),

    /**
     * 店铺存在余额不允许删除
     */
    SHOP_EXIST_BALANCE_NOT_DEL(80005, "shop.exist.balance.not.del"),

    /**
     * 店铺已不存在
     */
    SHOP_NOT_EXIST(80006, "shop.not.exist"),

    /**
     * 签约类目更新失败
     */
    SHOP_CATEGORY_UPDATE_FAIL(80007, "shop.category.update.fail"),

    /**
     * 已存在自营商家发货配置
     */
    SHOP_DELIVER_SETTINGS_EXISTED(80008, "shop.deliver.settings.existed"),

    /**
     * 参数校验失败
     */
    SHOP_LOGISTICS_ADDRESS_VALID_PARAM_FAIL(80009, "shop.logistics.address.valid.param.fail"),


    /*   todo 新增异常码 请在此行的上一行新增*/
    /*  todo 桑心病狂💥💥💥💥💥错误码胡乱定义 有时间后期优化*/

    /**
     * 校验模板参数失败
     */
    FAILED_TO_VALIDATE_TEMPLATE_PARAMETERS(800008, "failed.to.validate.template.parameters"),

    /**
     * 模板名称已经存在
     */
    TEMPLATE_NAME_ALREADY_EXIST(800009, "template.name.already.exist"),

    /**
     * 模板引用的页面不存在
     */
    THE_REFERENCED_PAGE_OF_TEMPLATE_NOT_EXIST(800010, "the.referenced.page.of.template.not.exist"),

    /**
     * 新增模板失败
     */
    FAILED_TO_ADD_TEMPLATE(800011, "failed.to.add.template"),

    /**
     * 模板不存在
     */
    TEMPLATE_NOT_EXIST(800012, "template.not.exist"),

    /**
     * 删除模板失败
     */
    FAILED_TO_DELETE_TEMPLATE(800013, "failed.to.delete.template"),

    /**
     * 修改模板状态失败
     */
    TEMPLATE_CAN_NOT_DELETE(800014, "template.can.not.delete"),

    /**
     * 修改模板状态失败
     */
    TEMPLATE_CAN_NOT_MODIFY(800015, "template.can.not.modify"),

    /**
     * 删除模板失败
     */
    FAILED_TO_UPDATE_TEMPLATE(80016, "failed.to.update.template"),

    /**
     * 修改模板状态失败
     */
    FAILED_TO_CHANGE_TEMPLATE_STATUS(800017, "failed.to.change.template.status"),

    /**
     * 页面名称已经存在
     */
    PAGE_NAME_ALREADY_EXIST(800018, "page.name.already.exist"),

    /**
     * 修改模板状态失败
     */
    FAILED_TO_CREATE_PAGE(800019, "failed.to.create.page"),

    /**
     * 页面不存在
     */
    PAGE_NOT_EXIST(800020, "page.not.exist"),

    /**
     * 页面不允许修改
     */
    PAGE_IS_NOT_ALLOWED_TO_BE_MODIFIED(800021, "page.is.not.allowed.to.be.modified"),

    /**
     * 修改页面失败
     */
    FAILED_TO_MODIFY_PAGE(800022, "failed.to.modify.page"),

    /**
     * 克隆页面失败
     */
    FAILED_TO_CLONE_PAGE(800023, "failed.to.clone.page"),

    /**
     * 删除页面失败
     */
    FAILED_TO_DELETE_PAGE(800024, "failed.to.delete.page"),

    /**
     * 页面被模版引用
     */
    REFERENCED_BY_TEMPLATE(800025, "referenced.by.template"),
    TEMPLATE_STATUS_CAN_NOT_MODIFY(800026, "template.status.can.not.modify"),

    TEMPLATE_NAME_LENGTH_TOO_LONG(800027, "template.name.length.too.long"),

    PAGE_NAME_LENGTH_TOO_LONG(800028, "page.name.length.too.long"),
    SHOP_EXIST_UNCOMPLETE_ORDER(800029, "shop.exist.uncomplete.order"),
    /**
     * 指定的系统管理员不存在
     */
    SHOP_SIGNED_ADMIN_NOT_FOUND(800030, "shop.signed.admin.not.found");

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
