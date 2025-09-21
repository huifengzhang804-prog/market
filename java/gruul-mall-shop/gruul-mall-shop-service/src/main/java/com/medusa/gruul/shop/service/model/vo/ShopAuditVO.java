package com.medusa.gruul.shop.service.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.shop.api.entity.Shop;
import com.medusa.gruul.shop.api.model.dto.ShopExtraDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author xiaoq
 * @since 2024/6/29
 * 店铺审核VO
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopAuditVO {


    /**
     * 店铺名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 状态 0.审核中,  1.正常, -1.禁用, -2审核拒绝
     */
    private ShopStatus status;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 附加数据
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private ShopExtraDTO extra;


    public static ShopAuditVO auditFormShop(Shop shop) {
        return new ShopAuditVO()
                .setExtra(shop.getExtra())
                .setCreateTime(shop.getCreateTime())
                .setStatus(shop.getStatus())
                .setCompanyName(shop.getCompanyName())
                .setName(shop.getName());
    }
}
