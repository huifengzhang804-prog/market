package com.medusa.gruul.addon.ic.modules.opens.judanke.model.printer;

import com.alibaba.fastjson2.annotation.JSONField;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.PrinterTemplate;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.Switch;
import com.medusa.gruul.common.fastjson2.FastJson2;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 张治保
 * @since 2024/8/6
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PrinterData {

    /**
     * order_seq	string	是
     * 订单序号
     */
    private String orderSeq;

    /**
     * shop_name	string	是
     * 店铺名称 聚单客测试店铺
     */
    private String shopName;

    /**
     * order_platform	string	是
     * 订单来源中文名称
     */
    private String orderPlatform;

    /**
     * deliver_time	string	是
     * 期望送达时间 2023-04-17 10:46:10
     */
    @JSONField(format = FastJson2.DATETIME_PATTEN)
    private LocalDateTime deliverTime;

    /**
     * booking	string	是
     * 是否预订单 0 及时单 1 预定单
     */
    private Switch booking;

    /**
     * order_time	string	是
     * 订单创建时间 2023-04-17 10:46:10
     */
    @JSONField(format = FastJson2.DATETIME_PATTEN)
    private LocalDateTime orderTime;

    /**
     * order_sn	string	是
     * 订单号
     */
    private String orderSn;

    /**
     * total_amount	string	是
     * 订单总金额 单位元
     */
    private BigDecimal totalAmount;

    /**
     * pickup_self	string	是
     * 是否是到店自取 0 配送 1 到店自取 到店自取 无需传收件地址信息
     */
    private Switch pickupSelf;

    /**
     * goods_amount	string	是
     * 商品总金额 单位元
     */
    private BigDecimal goodsAmount;

    /**
     * cost_amount	string	是
     * 总费用 单位元
     */
    private BigDecimal costAmount;

    /**
     * discount_amount	string	是
     * 总折扣 单位元
     */
    private BigDecimal discountAmount;

    /**
     * goods_total_number	string	是
     * 商品总数量
     */
    private Long goodsTotalNumber;

    /**
     * recipient	object	是
     * 收件人信息
     */
    private RecipientParam recipient;

    /**
     * shop_phone	string	否
     * 店铺手机号
     */
    private String shopPhone;

    /**
     * note	string	是
     * 备注
     */
    private String note;

    /**
     * goods	array	否
     * 商品信息
     */
    private List<GoodParam> goods;

    /**
     * cost	array	否
     * 费用信息
     */
    private List<CostParam> cost;

    /**
     * discount	array	否
     * 折扣信息
     */
    private List<CostParam> discount;

    /**
     * greeting	string	否
     * 祝福语
     */
    private String greeting;

    /**
     * buyer	string	是
     * 订购人
     */
    private String buyer;

    /**
     * print_setting	string	否
     * 打印设置
     */
    private PrintSettingParam printSetting;

    /**
     * tid	string	是
     * 打印任务ID 不可重复 30s内提交同一任务ID，会报不可重复提交
     */
    private String tid;

    /**
     * template_id	string	是
     * 模板ID
     */
    private PrinterTemplate templateId;
}
