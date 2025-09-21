package com.medusa.gruul.goods.api.model.enums;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author xiaoq
 * @Description GoodsError
 * @date 2023-06-17 15:40
 */
@Getter
@RequiredArgsConstructor
public enum GoodsError implements Error {
    /**
     * 虚拟配送校验失败
     */
    VIRTUAL_DISTRIBUTION_CHECK_ERROR(20000, "goods.virtual.distribution.check.error"),

    /**
     * 实物配送校验失败
     */
    REAL_DISTRIBUTION_CHECK_ERROR(20001, "goods.real.distribution.check.error"),

    /**
     * 当前信息不存在
     */
    CURRENT_INFO_NOT_EXIST(20002, "goods.current.info.not.exist"),

    /**
     * 产品特性删除失败
     */
    PRODUCT_FEATURE_DELETED_FAIL(20003, "goods.product.feature.deleted.fail"),

    /**
     * 参数错误,请检查必填项
     */
    PARAMETER_ERROR_CHECK_REQUIRED(20004, "goods.parameter.error.check.required"),

    /**
     * 您已关注该店铺
     */
    YOU_FOLLOWED_THIS_SHOP(2005, "goods.followed.this.shop"),

    /**
     * 当前商品不存在
     */
    CURRENT_GOODS_NOT_EXIST(20006, "goods.current.goods.not.exist"),

    /**
     * 请勿传递重复属性
     */
    ATTRIBUTES_REPETITION(20007, "goods.attributes.repetition"),

    /**
     * 当前商品属性不存在
     */
    CURRENT_GOODS_ATTRIBUTES_NOT_EXIST(20008, "goods.current.goods.attributes.not.exist"),

    /**
     * 产品属性发生变化,请稍后再试
     */
    PRODUCT_ATTRIBUTES_HAVE_CHANGED(20009, "goods.product.attributes.have.changed"),
    /**
     * 该分类存在绑定商品,不能删除
     */
    CLASSIFY_UNABLE_DELETE(20010, "goods.classify.unable.delete"),

    /**
     * 供应商审核失败
     */
    SUPPLIER_AUDIT_FAIL(20011, "goods.supplier.audit.fail"),

    /**
     * 供应商已被删除
     */
    SUPPLIER_HAVE_DELETE(20012, "goods.supplier.have.delete"),

    /**
     * 供应商新增失败
     */
    SUPPLIER_ADD_FAIL(20013, "goods.supplier.add.fail"),

    /**
     * 供应商更新失败
     */
    SUPPLIER_UPDATE_FAIL(20014, "goods.supplier.update.fail"),

    /**
     * 该手机号已是供应商
     */
    PHONE_ALREADY_SUPPLIER(20015, "goods.phone.already.supplier"),

    /**
     * 该手机号已是供应商,但被禁用！
     */
    PHONE_ALREADY_SUPPLIER_BUT_FORBIDDEN(20016, "goods.already.supplier.but.forbidden"),

    /**
     * 商品发布失败！
     */
    PRODUCT_ISSUE_FAIL(20017, "goods.product.issue.fail"),

    /**
     * 商品删除失败
     */
    PRODUCT_DELETE_FAIL(20018, "goods.product.delete.fail"),


    /**
     * 商品修改失败
     */
    PRODUCT_UPDATE_FAIL(20019, "goods.product.update.fail"),

    /**
     * 链接错误，请检查链接
     */
    LINK_ERROR(20020, "goods.link.error"),

    /**
     * 分类更新更新数据失败
     */
    CLASSIFY_UPDATE_FAIL(20021, "goods.classify.update.fail"),

    /**
     * 分类数据不存在
     */
    CLASSIFY_DATA_NOT_EXIST(20022, "goods.classify.data.not.exist"),

    /**
     * 添加分类不可与上级分类同名
     */
    CLASSIFY_NAME_REPETITION(20023, "goods.classify.name.repetition"),

    /**
     * 同级分类下,已存在同名分类
     */
    SIBLING_CLASSIFY_NAME_REPETITION(20024, "goods.sibling.classify.name.repetition"),


    /**
     * 分类保存失败
     */
    CLASSIFY_SAVE_FAIL(20021, "goods.classify.save.fail"),

    /**
     * 平台下架商品不可上架,如需上架请联系管理员
     */
    PRODUCT_STATUS_OPERATE_EXCEPTION(20022, "goods.status.operate.exception"),

    /**
     * categoryImg不能为空
     */
    CATEGORY_IMG_NOT_EXIST(20023, "goods.category.img.not.exist"),

    /**
     * categoryImg不符合格式要求
     */
    CATEGORY_BMP_FAIL(20024, "goods.category.bmp.fail"),

    /**
     * 当前设置类目名称存在重复请检查,类目名称:
     */
    SET_SIBLING_CLASSIFY_NAME_REPETITION(20024, "goods.set.sibling.classify.name.repetition"),

    /**
     * 产品属性必选校验失败
     */
    PRODUCT_ATTRIBUTES_REQUIRED_CHECK_FAIL(20025, "goods.product.attributes.required.check.fail"),

    PRODUCT_ATTRIBUTES_RADIO_CHECK_FAIL(20026, "goods.product.attributes.radio.check.fail"),

    /**
     * 99 API异常
     */
    API_EXCEPTION_99API(20027, "goods.api.exception.99api"),

    /**
     * 当前删除商品存在未下架商品,不可删除
     */
    PRODUCT_CANNOT_BE_DELETED(20028, "goods.product.cannot.be.deleted"),


    /**
     * 当前商品不可进行上架操作
     */
    CURRENT_PRODUCT_NOT_AVAILABLE(20029, "goods.current.product.not.available"),

    /**
     * 供应商不可用
     */
    SUPPLIER_INVALID(20030, "goods.supplier.invalid"),

    /**
     * 当前代销商品不存在,或已被违规下架
     */
    CURRENT_CONSIGNMENT_PRODUCT_NOT_EXIST_OR_EXCEPTION(20031,
            "goods.current.consignment.product.not.exist.or.exception"),

    /**
     * 商品标签名称已存在
     */
    LABEL_NAME_EXISTS(20032, "goods.label.name.exists"),

    /**
     * 代销商品不支持修改
     */
    CONSIGNMENT_PRODUCT_NOT_SUPPORT_MODIFY(20033, "goods.consignment.product.not.support.modify"),
    /**
     * 商品状态不正确
     */
    PRODUCT_STATUS_NOT_CORRECT(20034, "goods.product.status.not.correct"),

    ;


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

    @Override
    public String msg(Object... args) {
        return I18N.msg(getMsgCode(), args);
    }

}
