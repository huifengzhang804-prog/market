package com.medusa.gruul.user.service.model.dto;

import com.medusa.gruul.user.api.model.dto.RelevancyRightsDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 免费会员dto
 *
 * @author xiaoq
 * @Description UserFreeMemberDTO.java
 * @date 2022-11-11 10:26
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserFreeMemberDTO {

    private Long id;

    /**
     * 免费会员名称
     */
    @NotBlank
    private String freeMemberName;

    /**
     * 所需成长值
     */
    @Min(0)
    private Long needValue;

    /**
     * 权益信息
     */
    @Valid
    private List<RelevancyRightsDTO> relevancyRightsList;
}
