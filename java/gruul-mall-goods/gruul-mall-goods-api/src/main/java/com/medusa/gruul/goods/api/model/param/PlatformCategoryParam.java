package com.medusa.gruul.goods.api.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.goods.api.model.vo.ApiPlatformProductVO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @Description: 平台类目param
 * @Author: xiaoq
 * @Date : 2022-05-18 18:27
 */
@Data
public class PlatformCategoryParam extends Page<ApiPlatformProductVO> {

    /**
     * 所属平台类目id
     */
    @NotNull
    private Long platformCategoryId;

    /**
     * 商品名称
     */
    private String productName;
}
