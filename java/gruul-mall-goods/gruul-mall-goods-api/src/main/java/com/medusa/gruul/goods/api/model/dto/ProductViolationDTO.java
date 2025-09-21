package com.medusa.gruul.goods.api.model.dto;

import com.medusa.gruul.goods.api.model.enums.ViolationType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 产品违规dto
 *
 * @author xiaoq
 * @Description ProductViolationDTO.java
 * @date 2023-06-15 15:31
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductViolationDTO implements Serializable {

    /**
     * 违规说明
     */
    private String violationExplain;

    /**
     * 违规证据
     */
    private String violationEvidence;


    /**
     * 违规类型
     */
    private List<ViolationType> violationType;

    /**
     * 检查人
     */
    private String rummager;

    /**
     * 检查时间
     */
    private LocalDateTime examineDateTime;

}
