package com.medusa.gruul.addon.team.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.team.mp.entity.TeamOrder;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2023/3/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class TeamOrderUserPageDTO extends Page<TeamOrder> {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 拼团团号
     */
    @NotBlank
    private String teamNo;

}
