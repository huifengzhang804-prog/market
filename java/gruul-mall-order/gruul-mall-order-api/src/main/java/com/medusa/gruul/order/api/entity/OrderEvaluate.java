package com.medusa.gruul.order.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单评论
 *
 * @author 张治保
 * @since 2022-09-05
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@TableName(value = "t_order_evaluate", autoResultMap = true)
public class OrderEvaluate extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

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
    @TableField("`name`")
    private String name;
    /**
     * 商品类型
     */
    @TableField("product_type")
    private ProductType productType;

    /**
     * 商品 sku id
     */
    private Long skuId;

    /**
     * 商品sku图片
     */
    private String image;

    /**
     * 规格
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> specs;

    /**
     * 评分
     */
    private Integer rate;

    /**
     * 评论
     */
    @TableField("`comment`")
    private String comment;

    /**
     * 评论图片/视频
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> medias;

    /**
     * 是否是精选评价
     * excellentTime;
     */
    private Boolean isExcellent;

    /**
     * 卖家回复
     */
    private String shopReply;

    /**
     * 买家回复时间 reply_time
     */
    private LocalDateTime replyTime;


    /**
     * 商品销售类型
     */
    private SellType sellType;


    /**
     * 评论人数
     */
    @TableField(exist = false)
    private Long evaluatePerson;


    /**
     * 订单创建时间
     */
    private LocalDateTime orderCreateTime;
}
