package com.medusa.gruul.addon.ic.modules.opens.judanke.model.shop;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/8/6
 */
@Getter
@Setter
@ToString
public class CreateResp implements Serializable {
    /**
     * 店铺 id
     */
    private Long shopId;
}
