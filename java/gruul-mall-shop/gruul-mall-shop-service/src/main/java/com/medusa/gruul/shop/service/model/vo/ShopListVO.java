package com.medusa.gruul.shop.service.model.vo;

import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.overview.api.model.ShopBalanceVO;
import com.medusa.gruul.shop.api.enums.ExtractionType;
import com.medusa.gruul.shop.api.model.dto.ShopExtraDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author xiaoq
 * @since 2024/6/18
 */
@Getter
@Setter
@ToString
public class ShopListVO {

    private Long id;


    /**
     * 管理员用户id
     */
    private Long userId;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺编号
     */
    private String no;


    /**
     * 联系电话
     */
    private String contractNumber;

    /**
     * 状态 0.审核中,  1.正常, -1.禁用, -2审核拒绝
     */
    private ShopStatus status;

    /**
     * 店铺评分
     */
    private String score;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 店铺管理员手机号
     */
    private String userMobile;
    /**
     * 用户名
     */
    private String userName;

    /**
     * 联系地址
     */
    private String address;

    /**
     * logo url
     */
    private String logo;

    /**
     * 店铺余额
     */
    private ShopBalanceVO shopBalance;

    /**
     * 店铺类型
     */
    private ShopType shopType;

    /**
     * 抽佣类型
     */
    private ExtractionType extractionType;
    /**
     * 审核时间
     */
    private Date auditTime;
    /**
     * 经营模式
     */
    private ShopMode mode;

    private ShopExtraDTO extra;
    /**
     * 有上架商品且有库存
     */
    private Boolean hasOnSaleAndStock = false;
}
