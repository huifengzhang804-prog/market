package com.medusa.gruul.service.uaa.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.service.uaa.api.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.time.LocalDate;

/**
 * <p>
 *
 * </p>
 *
 * @author 张治保
 * @since 2022-03-08
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_user_data")
public class UserData extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别
     */
    private Gender gender;
    
    /**
     * 生日
     */
    private LocalDate birthday;

    private String paymentPsw;
    private String referralCode;
    private Long numUserId;
}
