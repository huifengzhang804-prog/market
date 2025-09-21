package com.medusa.gruul.goods.service.model.copy;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author miskw
 * @date 2023/2/3
 */
@Data
@Accessors(chain = true)
public class AlSku {
    /**
     * 规格id
     */
    private String fid;
    /**
     * 规格名称
     */
    private String prop;
    /**
     * 规格值
     */
    private List<AlPropDto> value;

    @Data
    public static class AlPropDto {
        private String imageUrl;
        private String name;
    }
}
