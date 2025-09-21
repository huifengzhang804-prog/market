package com.medusa.gruul.addon.integral.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.integral.model.enums.IntegralProductStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 积分商品查询参数
 *
 * @author xiaoq
 * @Description 积分商品查询参数
 * @date 2023-01-31 16:39
 */
@Getter
@Setter
@ToString
public class IntegralProductParam extends Page<Object> {

    /**
     * 是否是用户端
     */
    private Boolean isConsumer;
    /**
     * 积分商品状态
     */
    private IntegralProductStatus status;


    private String keyword;
}
