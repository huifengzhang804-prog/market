package com.medusa.gruul.user.api.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;


/**
 * 会员标签dto
 *
 * @author wufuzhong
 * @date 2023-12-04 10:00:00
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class MemberLabelDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8526717803586173955L;
    /**
     * 会员主键id
     */
    @NotNull
    private Long id;

    /**
     * 会员名称
     */
    private String name;

    /**
     * 会员名称字体颜色
     */
    @Size(max = 7)
    private String fontColor;

    /**
     * 会员名称标签颜色
     */
    @Size(max = 7)
    private String labelColor;

    /**
     * 会员价标签名称
     */
    @Size(max = 5)
    private String priceLabelName;

    /**
     * 会员价字体颜色
     */
    @Size(max = 7)
    private String priceFontColor;

    /**
     * 会员价标签颜色
     */
    @Size(max = 7)
    private String priceLabelColor;
}
