package com.medusa.gruul.addon.seckill.model.bo;

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
public class JobId {

    private Integer startId;
    private Integer endId;

}
