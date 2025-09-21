package com.medusa.gruul.addon.team.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 商品凑团列表
 *
 * @author 张治保
 * date 2023/3/15
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class TeamProductOrderPageDTO extends Page<Object> {

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

    /**
     * 商品id
     */
    @NotNull
    private Long productId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 团号
     */
    private String teamNo;

}
