package com.medusa.gruul.service.uaa.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 商品查询Param
 * @Author: xiaoq
 * @Date : 2022-03-10 10:20
 */
@Data
@Accessors(chain = true)
public class SalesSectionNoPageDTO  {

    private Long salesSectionId;

}
