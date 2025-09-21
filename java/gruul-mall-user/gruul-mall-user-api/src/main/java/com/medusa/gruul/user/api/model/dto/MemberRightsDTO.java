package com.medusa.gruul.user.api.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 会员权益dto
 *
 * @author xiaoq
 * @Description MemberRightsDTO.java
 * @date 2022-11-09 16:04
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class MemberRightsDTO {
    private Long id;

    /**
     * 权益名称
     */
    @NotBlank
    private String rightsName;

    /**
     * 权益图标
     */
    @NotBlank
    private String rightsIcon;

    /**
     * 权益说明
     */
    @NotBlank
    private String rightsExplain;


}
