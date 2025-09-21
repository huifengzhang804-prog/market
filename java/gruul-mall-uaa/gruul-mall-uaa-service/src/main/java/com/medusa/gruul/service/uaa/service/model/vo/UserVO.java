package com.medusa.gruul.service.uaa.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2022/4/28
 */
@Getter
@Setter
@ToString
public class UserVO {
    /**
     * id
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;
}
