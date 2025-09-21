package com.medusa.gruul.user.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.user.service.mp.entity.UserCollect;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author WuDi
 * date: 2022/11/4
 */
@Getter
@Setter
@ToString
public class UserCollectQueryDTO extends Page<UserCollect> {

    /**
     * 商品名称
     */
    private String productName;
}
