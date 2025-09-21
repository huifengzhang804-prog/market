package com.medusa.gruul.order.service.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;


/**
 * 用户评价
 *
 * @author wudi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderEvaluateItemVO {

    /**
     * 总评价id
     */
    private Long evaluateId;

    /**
     * 评价项id
     */
    private Long evaluateItemId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 包裹id
     */
    private Long packageId;

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
     * 商品id
     */
    private Long productId;

    /**
     * 订单商品项id
     */
    private Long itemId;

    /**
     * 商品名称
     */
    private String name;

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
    private String comment;

    /**
     * 评论图片/视频
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> medias;

    /**
     * 是否是精选评价
     */
    private Boolean isExcellent;

    /**
     * 设置精选时间
     */
    private LocalDateTime excellentTime;

    /**
     * 卖家回复
     */
    private String shopReply;


}
