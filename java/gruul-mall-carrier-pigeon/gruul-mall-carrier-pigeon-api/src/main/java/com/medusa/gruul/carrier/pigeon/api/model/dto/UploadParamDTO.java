package com.medusa.gruul.carrier.pigeon.api.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2023/9/25
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UploadParamDTO implements Serializable {

    /**
     * 文件格式
     */
    private String format;

    /**
     * 文件字节
     */
    private byte[] fileBytes;
}
