package com.medusa.gruul.addon.supplier.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.supplier.model.vo.PublishProductVO;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @author 张治保
 * date 2023/7/29
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PublishProductQueryDTO extends Page<PublishProductVO> {

    /**
     * 供应商 id
     */
    private Long supplierId;

    /**
     * 平台类目 id
     */
    private Long categoryId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 当前店铺 id
     */
    @Null
    private Long shopId;
}
