package com.medusa.gruul.carrier.pigeon.service.modules.geo.model.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * @author xiaoq
 * @description
 * @data: 2025/01/20
 */
@Data
public class GeoConfigDTO  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *  高德 JSAPI key
     */
    @NotNull(message = "高德 JSAPI key 不可为空")
    private  String   amapKey ;


    /**
     *  高德 JSAPI secre key 可为空  为空时 走nginx
     */
    private String amapsecretKey;

    /**
     * 高德 Web服务 key
     */
    @NotNull(message = "高德 Web服务 key 不可为空")
    private String amapWebServiceKey;


}
