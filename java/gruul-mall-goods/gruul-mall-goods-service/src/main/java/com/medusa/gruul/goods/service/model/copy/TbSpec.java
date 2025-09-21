package com.medusa.gruul.goods.service.model.copy;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 淘宝规格组
 */
@Data
@Accessors(chain = true)
public class TbSpec {
    private String vid;
    /**
     * 规格值
     */
    private String name;

}