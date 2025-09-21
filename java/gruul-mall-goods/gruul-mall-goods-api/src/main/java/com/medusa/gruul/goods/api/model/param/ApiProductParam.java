package com.medusa.gruul.goods.api.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * @Description: C端店铺商品查询param
 * @Author: xiaoq
 * @Date : 2022-05-19 13:55
 */
@Data
public class ApiProductParam extends Page<Object> {

    /**
     * 商品名称
     */
    private String productName;


}
