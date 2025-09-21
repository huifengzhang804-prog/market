package com.medusa.gruul.addon.seckill.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/5/30
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SeckillSkuVO {

    private Long id;

    private String specs;

    private Long stock;

    private Long price;

}
