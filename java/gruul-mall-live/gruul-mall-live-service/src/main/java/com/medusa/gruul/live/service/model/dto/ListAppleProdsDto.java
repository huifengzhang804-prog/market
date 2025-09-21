package com.medusa.gruul.live.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author miskw
 * @date 2022/11/16
 * @Describe 直播商品选择商品
 */
@Data
public class ListAppleProdsDto extends Page<Object> {
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 商品Ids
     */
    @NotNull(message = "商品Ids不能为空!")
    private List<Long> productIds;

}
