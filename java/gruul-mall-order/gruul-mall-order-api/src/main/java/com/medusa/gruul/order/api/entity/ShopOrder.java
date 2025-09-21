package com.medusa.gruul.order.api.entity;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.global.model.enums.InputType;
import com.medusa.gruul.order.api.enums.ShopOrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 店铺订单表
 *
 * @author 张治保
 * @since 2022-06-08
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "t_shop_order", autoResultMap = true)
@NoArgsConstructor
public class ShopOrder extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 店铺订单号
     * {主订单号}-{序号}
     */
    @TableField("`no`")
    private String no;
    /**
     * 店铺订单状态
     */
    @TableField("`status`")
    private ShopOrderStatus status;
    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺logo
     */
    private String shopLogo;

    /**
     * 店铺订单实际总额
     */
    private Long totalAmount;

    /**
     * 店铺订单实际运费总额
     */
    private Long freightAmount;

    /**
     * 店铺订单折扣总额
     */
    private Long discountAmount;

    /**
     * 店铺订单备注
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private Remark remark;

    /**
     * 额外信息
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private Extra extra;

    /* ** 以下为业务处理代码 ** */
    /**
     * 订单收货人
     */
    @TableField(exist = false)
    private OrderReceiver orderReceiver;

    /**
     * 店铺订单项
     */
    @TableField(exist = false)
    private List<ShopOrderItem> shopOrderItems;
    /**
     * 店铺订单项
     * 根据skuId分组
     * 将同一个sku的订单项合并到同一个skuGroupShopOrderItems中
     * 属于上面拆单的逆操作
     */
    @TableField(exist = false)
    private Map<Long, List<ShopOrderItem>> skuGroupShopOrderItems;

    /**
     * 关联订单信息
     */
    @ToString.Exclude
    @TableField(exist = false)
    private Order order;

    /**
     * 店铺类型
     */
    @TableField(exist = false)
    private ShopType shopType;

    /**
     * 店铺运营模式
     */
    @TableField(exist = false)
    private ShopMode shopMode;

    public Long payAmount() {
        long total = getTotalAmount();
        total = total < 0 ? 0 : total;
        long freight = getFreightAmount();
        freight = freight < 0 ? 0 : freight;
        long discount = getDiscountAmount();
        discount = discount < 0 ? 0 : discount;
        return Math.max(0, total - discount) + freight;
    }

    /**
     * 只获取备注信息 不获取其它表单信息
     *
     * @return 备注信息
     */
    public @NotNull String remarkOnly() {
        List<RemarkItem> formItems;
        if (remark == null || CollUtil.isEmpty(formItems = remark.getItems())) {
            return StrUtil.EMPTY;
        }
        for (RemarkItem formItem : formItems) {
            if (formItem.getType() == InputType.REMARK) {
                return formItem.getValue();
            }
        }
        return StrUtil.EMPTY;
    }

    /**
     * 店铺备注表单信息
     */
    @Getter
    @Setter
    @ToString
    @Accessors(chain = true)
    public static class Remark implements Serializable {

        /**
         * 是否开启订单播报
         */
        private Boolean orderNotify;

        /**
         * 平台备注
         */
        private String platform;

        /**
         * 店铺备注
         */
        private String shop;

        /**
         * 供应商备注
         */
        private String supplier;

        /**
         * 用户输入的表单数据
         */
        private List<RemarkItem> items;

        /**
         * 清除无关数据
         */
        public void clearDistractor() {
            setOrderNotify(null)
                    .setPlatform(null)
                    .setShop(null)
                    .setSupplier(null);
        }

    }

    @Getter
    @Setter
    @ToString
    @Accessors(chain = true)
    public static class RemarkItem implements Serializable {
        /**
         * 表单名称
         */
        private String key;

        /**
         * 表单值
         */
        private String value;

        /**
         * 表单类型
         */
        private InputType type;
    }

    /**
     * 店铺订单额外信息
     */
    @Getter
    @Setter
    @ToString
    @Accessors(chain = true)
    public static class Extra implements Serializable {
        /**
         * 初始化的空对象
         */
        public static final Extra EMPTY = new Extra();

        /**
         * 取件码
         */
        private Long pickupCode;

        /**
         * 是否可确认收货
         */
        private Boolean icReceivable;

        /**
         * 发货时间
         */
        private LocalDateTime deliverTime;

        /**
         * 可确认收货时间
         */
        private LocalDateTime receivableTime;

        /**
         * 收货时间
         */
        private LocalDateTime receiveTime;


    }

}
