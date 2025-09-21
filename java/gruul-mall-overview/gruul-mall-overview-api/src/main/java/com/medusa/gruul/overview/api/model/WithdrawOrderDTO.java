package com.medusa.gruul.overview.api.model;

import cn.hutool.core.lang.RegexPool;
import com.medusa.gruul.overview.api.enums.OwnerType;
import com.medusa.gruul.overview.api.enums.WithdrawSourceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 提现工单消息
 *
 * @author 张治保
 * date 2022/11/21
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class WithdrawOrderDTO {

    /**
     * 来源类型
     */
    @NotNull
    private WithdrawSourceType sourceType;

    /**
     * 提现者类型
     */
    @NotNull
    private OwnerType ownerType;

    /**
     * 用户id/店铺id 由提现者类型决定
     */
    @NotNull
    private Long ownerId;

    /**
     * 名称 /分销员为分销员姓名 店铺为店铺名称
     */
    @NotBlank
    private String name;

    /**
     * 头像 分销员为用户头像 店铺为店铺logo
     */
    @NotBlank
    @Pattern(regexp = RegexPool.URL_HTTP)
    private String avatar;

    /**
     * 联系方式
     */
    @NotBlank
    private String contact;


    /**
     * 提现方式与参数
     */
    @NotNull
    @Valid
    private DrawTypeModel drawType;
    /**
     * 申请人ID
     */
    @NotNull
    private Long applyUserId;
//    /**
//     * 申请人昵称
//     */
//    @NotNull
//    private String applyUserName;
    /**
     * 申请人手机号
     */
    @NotNull
    private String applyUserPhone;


}
