package com.medusa.gruul.order.service.model.enums;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 订单错误码 范围 30000 - 39999
 *
 * @author 张治保
 * date 2023/6/17
 */
@Getter
@RequiredArgsConstructor
public enum OrderError implements Error {

    /**
     * 收货人不能为空
     */
    RECEIVER_NOT_NULL(30000, "order.receiver.not.null"),

    /**
     * 物流公司不能为空
     */
    LOGISTICS_COMPANY_NOT_NULL(30001, "order.logistics.company.not.null"),

    /**
     * 物流公司名称与物流公司代码不能为空
     */
    LOGISTICS_COMPANY_NAME_AND_CODE_NOT_NULL(30002, "order.logistics.company.name.and.code.not.null"),

    /**
     * 快递单号不能为空
     */
    EXPRESS_NUMBER_NOT_NULL(30003, "order.express.number.not.null"),

    /**
     * 发货地址不能为空
     */
    SENDER_ADDRESS_NOT_NULL(30004, "order.sender.address.not.null"),

    /**
     * 店铺表单校验失败
     */
    SHOP_ORDER_FORM_VALID_FAIL(30005, "order.shop.order.form.valid.fail"),

    /**
     * 商品不可用
     */
    GOODS_NOT_AVAILABLE(30006, "order.goods.not.available"),

    /**
     * 商品库存不足
     */
    GOODS_STOCK_NOT_ENOUGH(30007, "order.goods.stock.not.enough"),

    /**
     * 商品超过限购次数
     */
    GOODS_OUT_OF_LIMIT(30008, "order.goods.out.of.limit"),

    /**
     * 备货单不存在
     */
    STORE_STOCK_ORDER_NOT_EXIST(30009, "order.store.stock.order.not.exist"),

    /**
     * 门店核销订单不存在
     */
    STORE_ORDER_NOT_EXIST(30010, "order.store.order.not.exist"),

    /**
     * 订单不存在
     */
    ORDER_NOT_EXIST(30011, "order.order.not.exist"),

    /**
     * 订单已支付
     */
    ORDER_PAID(30012, "order.order.paid"),

    /**
     * 订单已无法支付
     */
    ORDER_CANNOT_PAY(30013, "order.order.cannot.pay"),

    /**
     * 订单已关闭
     */
    ORDER_CLOSED(30014, "order.order.closed"),

    /**
     * 订单无法发起售后
     */
    ORDER_CANNOT_AFS(30015, "order.order.cannot.afs"),

    /**
     * 订单售后状态以改变
     */
    ORDER_AFS_STATUS_CHANGED(30016, "order.order.afs.status.changed"),

    /**
     * 商品未发货 不支持该售后类型
     */
    GOODS_NOT_DELIVERED_NOT_SUPPORT_AFS_TYPE(30017, "order.goods.not.delivered.not.support.afs.type"),

    /**
     * 订单状态不正确
     */
    INVALID_ORDER_STATUS(30018, "order.invalid.order.status"),

    /**
     * 不包含待发货商品
     */
    NOT_CONTAIN_WAIT_DELIVER_GOODS(30019, "order.not.contain.wait.deliver.goods"),

    /**
     * 订单未支付
     */
    ORDER_NOT_PAID(30020, "order.order.not.paid"),

    /**
     * 售后无法关闭
     */
    AFS_CANNOT_CLOSE(30021, "order.afs.cannot.close"),

    /**
     * 含有非待发货的商品
     */
    CONTAIN_NOT_WAIT_DELIVER_GOODS(30022, "order.contain.not.wait.deliver.goods"),

    /**
     * 拆分数量设置有误
     */
    SPLIT_NUM_ERROR(30023, "order.split.num.error"),

    /**
     * 订单暂不可评价
     */
    ORDER_CANNOT_COMMENT(30024, "order.cannot.comment"),

    /**
     * 订单已评价
     */
    ORDER_COMMENTED(30025, "order.commented"),

    /**
     * 订单未评价
     */
    ORDER_NOT_COMMENTED(30026, "order.not.commented"),

    /**
     * 用户资料不完整
     */
    USER_INFO_NOT_COMPLETE(30026, "order.user.info.not.complete"),

    /**
     * 不支持的配送方式
     */
    NOT_SUPPORT_DISTRIBUTION_MODE(30027, "order.not.support.distribution.mode"),

    /**
     * 无法给代销商品发货
     */
    CONSIGNMENT_GOODS_CANNOT_DELIVER(30028, "order.consignment.goods.cannot.deliver"),

    /**
     * 砍价订单只能购买一次
     */
    ORDER_BARGAIN_ONLY_BUY_ONE(30029, "order.bargain.only.but.one"),

    /**
     * 收货地址 不存在
     */
    RECEIVER_NOT_EXIST(30030, "receiver.not.exist"),
    /**
     * 用户订单上传失败
     */
    USER_ORDER_UPLOADER_ERROR(30031, "user.order.uploader.error"),

    /**
     * 订单平台类型错误
     */
    ORDER_PLATFORM_ERROR(30032, "order.platform.error"),

    /**
     * 订单平台发货类型错误
     */
    ORDER_PLATFORM_DELIVER_TYPE_ERROR(30033, "order.platform.deliver.type.error"),

    /**
     * 微信小程序订单限制10个
     */
    MINI_APP_ORDER_LIMIT_TEN(30034, "order.mini.order.limit.ten"),

    /**
     * 同城订单不支持拆单
     */
    IC_ORDER_CANNOT_SPLIT(30035, "order.ic.cannot.split"),

    /**
     * 还有待发货商品，暂无法确认收货
     */
    CONTAIN_NOT_WAIT_DELIVER_GOODS_NOT_CONFIRM(30036, "order.contain.not.wait.deliver.goods.not.confirm"),

    /**
     * 当前用户不支持购买商品
     */
    CURRENT_USER_NONSUPPORT_BUY(30037, "order.current.user.nonsupport.buy"),

    /**
     * 店铺不可用
     */
    SHOP_NOT_AVAILABLE(30038, "order.shop.not.available"),

    /**
     * sku 不可用 查询无数据、商品库存不足、商品超限购
     */
    SKU_NOT_AVAILABLE(30039, "order.sku.not.available"),

    /**
     * 活动已结束
     */
    ACTIVITY_END(30040, "order.activity.end"),


    /**
     * 打印机添加失败
     */
    PRINTER_ADD_FAILED(30041, "order.printer.add.failed"),

    /**
     * 不支持的打印机
     */
    PRINTER_NOT_SUPPORT(30042, "order.printer.not.support"),

    /**
     * 打印机解绑失败
     */
    PRINTER_UNBIND_ERROR(30043, "order.printer.unbind.error"),

    /**
     * 打印机和打印模板尺寸不匹配
     */
    PRINTER_SIZE_NOT_MATCHED(30044, "order.printer.size.notMatched"),

    /**
     * 已存在相同的打印类型，同一打印类型的打印任务只能添加一次
     */
    PRINTER_TASK_EXIST_SAME_LINK(30045, "order.printer.task.existSameLink"),

    /**
     * 已存在相同的打印类型，同一打印类型的打印任务只能添加一次
     */
    PRINTER_CANNOT_DELETE(30046, "order.printer.cannotDelete"),

    /**
     * 不能重复添加同一台打印机
     */
    PRINTER_CANNOT_ADD_SAME(30047, "order.printer.cannotAddSame"),

    /**
     * 打印机开放平台配置不正确
     */
    PRINTER_WRONG_OPEN_CONFIG(30048, "order.printer.wrongOpenConfig"),

    /**
     * 打印机开放平台配置不正确
     */
    PRINTER_PLATFORM_NOT_CONFIG(30049, "order.printer.platformNotConfig"),

    /**
     * 用户没有评论权限
     */
    USER_CAN_NOT_COMMENT(30050, "user.can.not.comment"),

    RECEIVER_AREA_ERROR(30051, "receiver.area.error" ),
    MIN_APP_DELIVER_NOT_OPEN(30052, "order.min.app.deliver.not.open"),
    MIN_APP_DELIVER_CONFIRM_NOT_SUPPORT(30053, "order.min.app.deliver.confirm.not.support")
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
}
