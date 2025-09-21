package com.medusa.gruul.shop.service.model.vo;

import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.overview.api.model.ShopBalanceVO;
import com.medusa.gruul.shop.api.enums.ExtractionType;
import com.medusa.gruul.shop.api.model.dto.ShopExtraDTO;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 张治保
 * date 2022/4/15
 */
@Getter
@Setter
@ToString
public class ShopVO {

    private Long id;

    /**
     * 店铺经营模式
     */
    private ShopMode shopMode;

    /**
     * 商户名称
     */
    private String companyName;
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
     * 定位
     */
    private Point location;

    /**
     * 省市区
     */
    private List<String> area;

    /**
     * 联系地址
     */
    private String address;

    /**
     * logo url
     */
    private String logo;

    /**
     * 管理员用户id
     */
    private Long userId;

    /**
     * 店铺管理员手机号
     */
    private String userMobile;

    /**
     * 介绍
     */
    private String briefing;

    /**
     * 上新公告
     */
    private String newTips;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 版本号
     */
    private Integer version;

    /**
     * B
     * 店铺司法注册信息Id
     */
    private ShopRegisterInfoVO registerInfo;

    /**
     * 银行账户信息
     */
    private ShopBankAccountVO bankAccount;

    /**
     * 店铺评分
     */
    private String score;

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
     * 抽佣百分比
     */
    private Integer drawPercentage;

    private ShopExtraDTO extra;
}
