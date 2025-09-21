package com.medusa.gruul.user.api.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 关联权益dto
 *
 * @author xiaoq
 * @Description RelevancyRightsDTO.java
 * @date 2022-11-10 19:16
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RelevancyRightsDTO {

    /**
     * 会员id
     */
    private Long memberId;


    /**
     * 会员权益id
     */
    private Long memberRightsId;

    /**
     * 会员权益扩展值
     */
    @Min(10)
    @Max(999)
    private Long extendValue;
}
