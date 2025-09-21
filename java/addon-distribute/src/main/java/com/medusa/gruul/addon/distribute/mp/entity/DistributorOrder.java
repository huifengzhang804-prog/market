package com.medusa.gruul.addon.distribute.mp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.distribute.model.enums.DistributeOrderStatus;
import com.medusa.gruul.addon.distribute.model.enums.Level;
import com.medusa.gruul.addon.distribute.model.enums.ShareType;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分销订单关联表
 *
 * @author 张治保
 * @since 2022-11-17
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_distributor_order", autoResultMap = true)
public class DistributorOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品类型
     */
    @TableField("product_type")
    private ProductType productType;

    /**
     * 规格id
     */
    private Long skuId;

    /**
     * sku图片
     */
    private String image;

    /**
     * sku规格
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> specs;

    /**
     * 商品数量
     */
    private Integer num;

    /**
     * 成交价
     */
    private Long dealPrice;

    /**
     * 订单状态
     */
    private DistributeOrderStatus orderStatus;

    /**
     * 是否分销内购订单
     */
    private Boolean purchase;

    /**
     * 当前订单所属一级分销商用户id
     */
    private Long oneId;

    /**
     * 一级分佣
     */
    private Long one;

    /**
     * 当前订单所属二级分销商用户id
     */
    private Long twoId;

    /**
     * 二级分佣
     */
    private Long two;

    /**
     * 当前订单所属三级分销商用户id
     */
    private Long threeId;

    /**
     * 三级分佣
     */
    private Long three;

    /**
     * 分佣类型
     */
    private ShareType shareType;

    /**
     * 一级分佣配置
     */
    private Long shareOne;

    /**
     * 二级分佣配置
     */
    private Long shareTwo;

    /**
     * 三级分佣配置
     */
    private Long shareThree;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


    /**
     * 订单对应的分销员
     */
    @TableField(exist = false)
    private Distributor distributor;

    /**
     * 预估佣金
     */
    @TableField(exist = false)
    private Long bonus;

    /**
     * 分销客户等级
     */
    @TableField(exist = false)
    private Level level;

    public DistributorOrder cloneIt() {
        return new DistributorOrder()
                .setUserId(this.userId)
                .setOrderNo(this.orderNo)
                .setShopId(this.shopId)
                .setProductId(this.productId)
                .setProductName(this.productName)
                .setSkuId(this.skuId)
                .setImage(this.image)
                .setSpecs(this.specs)
                .setNum(this.num)
                .setDealPrice(this.dealPrice)
                .setOrderStatus(this.orderStatus)
                .setPurchase(this.purchase)
                .setOneId(this.oneId)
                .setOne(this.one)
                .setTwoId(this.twoId)
                .setTwo(this.two)
                .setThreeId(this.threeId)
                .setThree(this.three)
                .setCreateTime(this.createTime);
    }
}
