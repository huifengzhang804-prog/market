package com.medusa.gruul.common.security.model.bean;

import com.medusa.gruul.common.system.model.model.ClientType;
import lombok.*;

/**
 * @author 张治保
 * @since 2024/5/22
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TokenKey implements java.io.Serializable {
    /**
     * 客户端类型
     */
    private ClientType clientType;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * token id
     */
    private String tokenId;

}
